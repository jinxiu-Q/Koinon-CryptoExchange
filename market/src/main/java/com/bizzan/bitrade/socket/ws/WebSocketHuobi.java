package com.bizzan.bitrade.socket.ws;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.entity.KLine;
import com.bizzan.bitrade.service.KlineRobotMarketService;
import com.bizzan.bitrade.util.JSONUtils;
import com.bizzan.bitrade.util.ZipUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.List;

public class WebSocketHuobi extends WebSocketClient {

    private Logger logger = LoggerFactory.getLogger(WebSocketClient.class);

    private KlineRobotMarketService marketService;

    public static String DEPTH = "market.%s.depth.step0"; // 深度
    public static String KLINE = "market.%s.kline.%s"; // K线
    public static String DETAIL = "market.%s.detail"; // 市场概要（最新价格、成交量等）
    public static String TRADE = "market.%s.trade.detail"; // 成交明细

    @Autowired
    private TaskExecutor taskExecutor;
    private double VOLUME_PERCENT = 0.13; // 火币成交量的百分比

    public static String PERIOD[] = { "1min", "5min", "15min", "30min", "60min","4hour", "1day", "1mon", "1week" };

    public WebSocketHuobi(URI uri, KlineRobotMarketService service) {
        super(uri);
        this.uri = uri;
        this.marketService = service;
    }

    @Override
    public void onOpen(ServerHandshake shake) {
    }


    // 同步K线
    public void reqKLineList(String symbol, String period, long from, long to) {
        String topic = String.format(KLINE, symbol.replace("/", "").toLowerCase(), period);
        // Huobi Websocket要求单次请求数据不能超过300条，因此需要在次对请求进行拆分
        long timeGap = to - from; // 时间差
        long divideTime = 0;
        if(period.equals("1min")) {
            divideTime = 60 * 300; // 1分钟 * 300条
        }
        if(period.equals("5min")){
            divideTime = 5* 60 * 300;
        }
        if(period.equals("15min")) {
            divideTime = 15* 60 * 300;
        }
        if(period.equals("30min")) {
            divideTime = 30 * 60 * 300;
        }
        if(period.equals("60min")){
            divideTime = 60 * 60 * 300;
        }
        if(period.equals("4hour")){
            divideTime = 4 * 60 * 60 * 300;
        }
        if(period.equals("1day")){
            divideTime = 24 * 60 * 60 * 300;
        }
        if(period.equals("1week")){
            divideTime = 7 * 24 * 60 * 60 * 300;
        }
        if(period.equals("1mon")) {
            divideTime = 30 * 24 * 60 * 60 * 300;
        }

        if(timeGap > divideTime) {
            long times = timeGap % (divideTime) > 0 ?  (timeGap/(divideTime) + 1) : timeGap/(divideTime);
            long temTo = from;
            long temFrom = from;
            for(int i = 0; i < times; i++) {
                if(temTo + (divideTime) > to) {
                    temTo = to;
                }else{
                    temTo = temTo + (divideTime);
                }
                sendWsMarket("req", topic, temFrom, temTo);
                temFrom = temFrom + divideTime;
            }
        }else{
            sendWsMarket("req", topic, from, to);
        }
    }

    @Override
    public void onMessage(String arg0) {
        if (arg0 != null) {
            logger.info("[WebSocketHuobi] receive message: {}", arg0);
        }
    }

    @Override
    public void onError(Exception arg0) {
        logger.error("[WebSocketHuobi] has error ,the message is :: {}", arg0.getMessage());
        arg0.printStackTrace();
        String message = "";
        try {
            message = new String(arg0.getMessage().getBytes(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[WebSocketHuobi] has error ,the message is :: {}", message);
        }
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        logger.info("[WebSocketHuobi] connection close: {} - {} - {}", arg0, arg1, arg2);
        int tryTimes = 0;
        // 尝试20次
        logger.info("[WebSocketHuobi] 尝试重新连接，第 " + tryTimes + "次");
        if(this.getReadyState().equals(ReadyState.NOT_YET_CONNECTED) || this.getReadyState().equals(ReadyState.CLOSED) || this.getReadyState().equals(ReadyState.CLOSING)) {

            Runnable sendable = new Runnable() {
                @Override
                public void run() {
                    logger.info("[WebSocketHuobi] 开启重新连接");
                    reconnect();
                }
            };
            new Thread(sendable).start();
        }
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        try {
            String message = new String(ZipUtils.decompress(bytes.array()), "UTF-8");

            JSONObject jsonObject = JSONObject.parseObject(message);
            if (!"".equals(message)) {
                if (message.indexOf("ping") > 0) {
                    String pong = jsonObject.toString();
                    send(pong.replace("ping", "pong"));
                } else {

                    String id = "";
                    if(jsonObject.containsKey("ch")) {
                        id = jsonObject.getString("ch");
                        if (id == null || id.split("\\.").length < 3) {
                            return;
                        }
                    }
                    if(jsonObject.containsKey("rep")) {
                        id = jsonObject.getString("rep");
                        if (id == null || id.split("\\.").length < 3) {
                            return;
                        }
                    }
                    if(id.equals("")) {
                        return;
                    }
                    StringBuilder sb = new StringBuilder(id.split("\\.")[1]);
                    String symbol = "";
                    if(sb.indexOf("eth")>1){
                        symbol = sb.insert(sb.indexOf("eth"), "/").toString().toUpperCase();
                    }else if(sb.indexOf("btc")>1){
                        symbol = sb.insert(sb.indexOf("btc"), "/").toString().toUpperCase();
                    }else if(sb.indexOf("usdt")>1){
                        symbol = sb.insert(sb.indexOf("usdt"), "/").toString().toUpperCase();
                    }

                    String type = id.split("\\.")[2];

                    if(type.equals("kline")) {

                        String data = jsonObject.getString("data");
                        String period = id.split("\\.")[3];

                        List<KLine> list = new ArrayList<>();
                        if (null != data && !"".equals(data) && JSONUtils.isJsonArray(data)) {

                            BigDecimal price = null;

                            JSONArray klineList = jsonObject.getJSONArray("data");

                            for(int i = 0; i < klineList.size(); i++) {
                                JSONObject klineObj = klineList.getJSONObject(i);

                                BigDecimal open = (price==null || i==0)? klineObj.getBigDecimal("open") : price; // 收盘价
                                BigDecimal close = (price==null || i==klineList.size()-1) ? klineObj.getBigDecimal("close") : price; // 收盘价
                                BigDecimal high = price==null ? klineObj.getBigDecimal("high") : price; // 收盘价
                                BigDecimal low = price==null ? klineObj.getBigDecimal("low") : price; // 收盘价
                                BigDecimal amount = klineObj.getBigDecimal("amount"); // 收盘价
                                BigDecimal vol = klineObj.getBigDecimal("vol"); // 收盘价
                                int count = klineObj.getIntValue("count"); // 收盘价
                                long time = klineObj.getLongValue("id");

                                KLine kline = new KLine(period);
                                kline.setClosePrice(close);
                                kline.setCount(count);
                                kline.setHighestPrice(high);
                                kline.setLowestPrice(low);
                                kline.setOpenPrice(open);
                                kline.setTime(time*1000);
                                kline.setTurnover(amount.multiply(BigDecimal.valueOf(VOLUME_PERCENT)));
                                kline.setVolume(vol.multiply(BigDecimal.valueOf(VOLUME_PERCENT)));
                                marketService.saveKLine(symbol, kline);
                            }
                        }
                    }
                }
            }
        } catch (CharacterCodingException e) {
            e.printStackTrace();
            logger.error("[WebSocketHuobi] websocket exception: {}", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[WebSocketHuobi] websocket exception: {}", e.getMessage());
        }
    }

    public void sendWsMarket(String op, String topic) {
        JSONObject req = new JSONObject();
        req.put(op, topic);
        send(req.toString());
    }

    public void sendWsMarket(String op, String topic, long from, long to) {
        JSONObject req = new JSONObject();
        req.put(op, topic);
        req.put("from", from);
        req.put("to", to);
        send(req.toString());
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
