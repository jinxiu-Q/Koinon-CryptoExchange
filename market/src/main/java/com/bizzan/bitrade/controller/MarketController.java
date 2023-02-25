package com.bizzan.bitrade.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.component.CoinExchangeRate;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.processor.CoinProcessor;
import com.bizzan.bitrade.processor.CoinProcessorFactory;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.ExchangeCoinService;
import com.bizzan.bitrade.service.ExchangeTradeService;
import com.bizzan.bitrade.service.MarketService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
public class MarketController {
    @Autowired
    private MarketService marketService;
    @Autowired
    private ExchangeCoinService coinService;
    @Autowired
    private CoinService coinInfoService;
    @Autowired
    private CoinProcessorFactory coinProcessorFactory;
    @Autowired
    private ExchangeTradeService exchangeTradeService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CoinExchangeRate coinExchangeRate;


    /**
     * 获取支持的交易币种
     *
     * @return
     */
    @RequestMapping("symbol")
    public List<ExchangeCoin> findAllSymbol() {
        List<ExchangeCoin> coins = coinService.findAllVisible();
        return coins;
    }

    @RequestMapping("overview")
    public Map<String, List<CoinThumb>> overview() {
        log.info("/market/overview");
        Map<String, List<CoinThumb>> result = new HashMap<>();
        List<ExchangeCoin> recommendCoin = coinService.findAllByFlag(1);
        List<CoinThumb> recommendThumbs = new ArrayList<>();
        for (ExchangeCoin coin : recommendCoin) {
            CoinProcessor processor = coinProcessorFactory.getProcessor(coin.getSymbol());
            if (processor != null) {
                CoinThumb thumb = processor.getThumb();
                recommendThumbs.add(thumb);
            }
        }
        result.put("recommend", recommendThumbs);
        List<CoinThumb> allThumbs = findSymbolThumb();
        Collections.sort(allThumbs, (o1, o2) -> o2.getChg().compareTo(o1.getChg()));
        int limit = allThumbs.size() > 10 ? 10 : allThumbs.size();
        List<CoinThumb> rankThumbs = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            if (allThumbs.get(i).getChange().compareTo(BigDecimal.ZERO) > 0) {
                rankThumbs.add(allThumbs.get(i));
            }
        }
        result.put("changeRank", rankThumbs); // 涨幅榜
        Collections.sort(allThumbs, (o1, o2) -> 0 - o2.getChg().compareTo(o1.getChg()));
        int downLimit = allThumbs.size() > 10 ? 10 : allThumbs.size();
        List<CoinThumb> rankDownThumbs = new ArrayList<>();
        for (int i = 0; i < downLimit; i++) {
            if (allThumbs.get(i).getChange().compareTo(BigDecimal.ZERO) < 0) {
                rankDownThumbs.add(allThumbs.get(i));
            }
        }
        result.put("changeRankDown", rankDownThumbs); // 跌幅榜
        return result;
    }

    @RequestMapping("engines")
    public Map<String, Integer> engines() {
        Map<String, CoinProcessor> processorList = coinProcessorFactory.getProcessorMap();
        Map<String, Integer> symbols = new HashMap<String, Integer>();
        processorList.forEach((key, processor) -> {
            if (processor.isStopKline()) {
                symbols.put(key, 2);
            } else {
                symbols.put(key, 1);
            }
        });
        return symbols;
    }

    /**
     * 获取币种详情
     *
     * @param
     * @return
     */
    @RequestMapping("coin-info")
    public Coin findCoin(String unit) {
        Coin coin = coinInfoService.findByUnit(unit);
        coin.setColdWalletAddress("");//隐藏冷钱包地址
        coin.setDepositAddress("");
        //当前对美元USDT的价格
        BigDecimal rate = coinExchangeRate.getUsdRate(unit);
        coin.setUsdRate(rate.doubleValue());
        return coin;
    }

    /**
     * 获取C2C中USDT兑换人民币价格
     *
     * @param
     * @return
     */
    @RequestMapping("ctc-usdt")
    public MessageResult ctcUsdt() {
        MessageResult mr = new MessageResult(0, "success");
        BigDecimal latestPrice = coinExchangeRate.getUsdCnyRate();
        JSONObject obj = new JSONObject();
        obj.put("buy", latestPrice);
        // 0.015为1.5%的买卖差价
        obj.put("sell", latestPrice.subtract(latestPrice.multiply(new BigDecimal(0.011)).setScale(2, BigDecimal.ROUND_DOWN)));
        mr.setData(obj);
        return mr;
    }

    /**
     * 获取某交易对详情
     *
     * @param symbol
     * @return
     */
    @RequestMapping("symbol-info")
    public ExchangeCoin findSymbol(String symbol) {
        ExchangeCoin coin = coinService.findBySymbol(symbol);
        coin.setCurrentTime(Calendar.getInstance().getTimeInMillis());
        return coin;
    }

    @RequestMapping("symbol-infoex")
    public JSONObject findSymbolex(String symbol) {
        ExchangeCoin coin = coinService.findBySymbol(symbol);
        coin.setCurrentTime(Calendar.getInstance().getTimeInMillis());
        //
        Calendar calendar = Calendar.getInstance();
        long nowTime = calendar.getTimeInMillis();
        calendar.add(Calendar.HOUR_OF_DAY, -7 * 24);
        long firstTimeOfToday = calendar.getTimeInMillis();
        List<KLine> lines = marketService.findAllKLine(symbol, firstTimeOfToday, nowTime, "1day");
        double highest = 0;
        double lowest = 999999;
        double volume = 0;
        int count = 0;

        for (KLine line : lines) {
            if (line.getHighestPrice().doubleValue() > highest) {
                highest = line.getHighestPrice().doubleValue();
            }
            if (line.getLowestPrice().doubleValue() < lowest) {
                lowest = line.getLowestPrice().doubleValue();
            }
            volume = volume + line.getVolume().doubleValue();
            count = count + 1;
        }
        JSONObject json = (JSONObject) JSON.toJSON(coin);
        if (count > 0) {
            json.put("weekHighest", highest);
            json.put("weekLowest", lowest);
            json.put("weekVolume", volume / count);
        } else {
            json.put("weekHighest", 0);
            json.put("weekLowest", 0);
            json.put("weekVolume", 0);
        }
        return json;
    }

    /**
     * 获取币种缩略行情
     *
     * @return
     */
    @RequestMapping("symbol-thumb")
    public List<CoinThumb> findSymbolThumb() {
        List<ExchangeCoin> coins = coinService.findAllVisible();
        List<CoinThumb> thumbs = new ArrayList<>();
        for (ExchangeCoin coin : coins) {
            CoinProcessor processor = coinProcessorFactory.getProcessor(coin.getSymbol());
            CoinThumb thumb = processor.getThumb();
            thumb.setZone(coin.getZone());
            thumbs.add(thumb);
        }
        return thumbs;
    }

    @RequestMapping("symbol-thumb-trend")
    public JSONArray findSymbolThumbWithTrend() {
        List<ExchangeCoin> coins = coinService.findAllVisible();
        Calendar calendar = Calendar.getInstance();
        //将秒、微秒字段置为0
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        long nowTime = calendar.getTimeInMillis();
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        JSONArray array = new JSONArray();
        long firstTimeOfToday = calendar.getTimeInMillis();
        for (ExchangeCoin coin : coins) {
            CoinProcessor processor = coinProcessorFactory.getProcessor(coin.getSymbol());
            CoinThumb thumb = processor.getThumb();
            JSONObject json = (JSONObject) JSON.toJSON(thumb);
            json.put("coinScale", coin.getCoinScale());
            json.put("baseCoinScale", coin.getBaseCoinScale());
            json.put("zone", coin.getZone());
            List<KLine> lines = marketService.findAllKLine(thumb.getSymbol(), firstTimeOfToday, nowTime, "15min");
            JSONArray trend = new JSONArray();
            for (KLine line : lines) {
                trend.add(line.getClosePrice());
            }
            json.put("trend", trend);
            array.add(json);
        }
        return array;
    }

    /**
     * 获取币种历史K线
     *
     * @param symbol
     * @param from
     * @param to
     * @param resolution
     * @return
     */
    @RequestMapping("history")
    public JSONArray findKHistory(String symbol, long from, long to, String resolution) {
        String period = "";
        if (resolution.endsWith("H") || resolution.endsWith("h")) {
            period = resolution.substring(0, resolution.length() - 1) + "hour";
        } else if (resolution.endsWith("D") || resolution.endsWith("d")) {
            period = resolution.substring(0, resolution.length() - 1) + "day";
        } else if (resolution.endsWith("W") || resolution.endsWith("w")) {
            period = resolution.substring(0, resolution.length() - 1) + "week";
        } else if (resolution.endsWith("M") || resolution.endsWith("m")) {
            period = resolution.substring(0, resolution.length() - 1) + "month";
        } else {
            Integer val = Integer.parseInt(resolution);
            if (val < 60) {
                period = resolution + "min";
            } else {
                period = (val / 60) + "hour";
            }
        }
        List<KLine> list = marketService.findAllKLine(symbol, from, to, period);

        JSONArray array = new JSONArray();
        boolean startFlag = false;
        KLine temKline = null;
        for (KLine item : list) {
            // 此段处理是过滤币种开头出现0开盘/收盘的K线
            // if(!startFlag && item.getOpenPrice().compareTo(BigDecimal.ZERO) == 0) {
            // 	continue;
            // }else {
            // 	startFlag = true;
            // }
            // 中间段如果出现为0的现象，需要处理一下
            // if(item.getOpenPrice().compareTo(BigDecimal.ZERO) == 0) {
            // 	item.setOpenPrice(temKline.getClosePrice());
            // 	item.setClosePrice(temKline.getClosePrice());
            // 	item.setHighestPrice(temKline.getClosePrice());
            // 	item.setLowestPrice(temKline.getClosePrice());
            // }
            JSONArray group = new JSONArray();
            group.add(0, item.getTime());
            group.add(1, item.getOpenPrice());
            group.add(2, item.getHighestPrice());
            group.add(3, item.getLowestPrice());
            group.add(4, item.getClosePrice());
            group.add(5, item.getVolume());
            array.add(group);

            temKline = item;
        }
        return array;
    }

    @RequestMapping("history-day")
    public JSONObject findKHistoryDay(HttpServletRequest request, String symbol, long start, long count) {
        String period = "1day";
        long to = System.currentTimeMillis();
        long from = to - count * 24 * 60 * 60 * 1000;
        List<KLine> list = marketService.findAllKLine(symbol, from, to, period);

        DateFormat YYYYMMDD = new SimpleDateFormat("yyyyMMdd");

        String lang = request.getHeader("lang");
        if (lang == null || lang == "") {
            lang = "en";
        }
        if ("zh".equalsIgnoreCase(lang)) {
            YYYYMMDD.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        } else {
            YYYYMMDD.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        }

        JSONArray array = new JSONArray();
        for (KLine item : list) {
            JSONArray group = new JSONArray();
            group.add(0, Integer.parseInt(YYYYMMDD.format(item.getTime())));
            group.add(1, item.getOpenPrice());//昨收
            group.add(2, item.getOpenPrice());
            group.add(3, item.getHighestPrice());
            group.add(4, item.getLowestPrice());
            group.add(5, item.getClosePrice());
            group.add(6, item.getVolume());
            group.add(7, item.getTurnover());
            array.add(group);
        }
        JSONObject json = new JSONObject();
        json.put("data", array);
        json.put("symbol", symbol);
        return json;
    }

    @RequestMapping("history-minute")
    public JSONObject findKHistoryMinute(HttpServletRequest request, String symbol, long start, long count) {
        String period = "1min";
        long to = System.currentTimeMillis();
        long from = to - count * 60 * 1000;
        List<KLine> list = marketService.findAllKLine(symbol, from, to, period);

        DateFormat YYYYMMDD = new SimpleDateFormat("yyyyMMdd");
        DateFormat HHmm = new SimpleDateFormat("HHmm");
        //////
        String lang = request.getHeader("lang");
        if (lang == null || lang == "") {
            lang = "en";
        }
        if ("zh".equalsIgnoreCase(lang)) {
            YYYYMMDD.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            HHmm.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        } else {
            YYYYMMDD.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            HHmm.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        }
        //
        JSONArray array = new JSONArray();
        for (KLine item : list) {
            JSONArray group = new JSONArray();
            group.add(0, Integer.parseInt(YYYYMMDD.format(item.getTime())));
            group.add(1, item.getOpenPrice());//昨收
            group.add(2, item.getOpenPrice());
            group.add(3, item.getHighestPrice());
            group.add(4, item.getLowestPrice());
            group.add(5, item.getClosePrice());
            group.add(6, item.getVolume());
            group.add(7, item.getTurnover());
            group.add(8, Integer.parseInt(HHmm.format(item.getTime())));
            array.add(group);
        }
        JSONObject json = new JSONObject();
        json.put("data", array);
        json.put("symbol", symbol);
        return json;
    }

    /**
     * 查询最近成交记录
     *
     * @param symbol 交易对符号
     * @param size   返回记录最大数量
     * @return
     */
    @RequestMapping("latest-trade")
    public List<ExchangeTrade> latestTrade(String symbol, int size) {
        return exchangeTradeService.findLatest(symbol, size);
    }

    @RequestMapping("exchange-plate")
    public Map<String, List<TradePlateItem>> findTradePlate(String symbol) {
        //远程RPC服务URL,后缀为币种单位
        String serviceName = "SERVICE-EXCHANGE-TRADE";
        String url = "http://" + serviceName + "/monitor/plate?symbol=" + symbol;
        ResponseEntity<HashMap> result = restTemplate.getForEntity(url, HashMap.class);
        return (Map<String, List<TradePlateItem>>) result.getBody();
    }


    @RequestMapping("exchange-plate-mini")
    public Map<String, JSONObject> findTradePlateMini(String symbol) {
        //远程RPC服务URL,后缀为币种单位
        String serviceName = "SERVICE-EXCHANGE-TRADE";
        String url = "http://" + serviceName + "/monitor/plate-mini?symbol=" + symbol;
        ResponseEntity<HashMap> result = restTemplate.getForEntity(url, HashMap.class);
        return (Map<String, JSONObject>) result.getBody();
    }


    @RequestMapping("exchange-plate-full")
    public Map<String, JSONObject> findTradePlateFull(String symbol) {
        //远程RPC服务URL,后缀为币种单位
        String serviceName = "SERVICE-EXCHANGE-TRADE";
        String url = "http://" + serviceName + "/monitor/plate-full?symbol=" + symbol;
        ResponseEntity<HashMap> result = restTemplate.getForEntity(url, HashMap.class);
        return (Map<String, JSONObject>) result.getBody();
    }

    @GetMapping("add_dcitionary/{bond}/{value}")
    public MessageResult addDcitionaryForAdmin(@PathVariable("bond") String bond, @PathVariable("value") String value) {
        log.info(">>>>字典表数据已修改>>>修改缓存中的数据>>>>>bond>" + bond + ">>>>>value>>" + value);
        String key = SysConstant.DATA_DICTIONARY_BOUND_KEY + bond;
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object bondvalue = valueOperations.get(key);
        if (bondvalue == null) {
            log.info(">>>>>>缓存中无数据>>>>>");
            valueOperations.set(key, value);
        } else {
            log.info(">>>>缓存中有数据>>>");
            valueOperations.getOperations().delete(key);
            valueOperations.set(key, value);
        }
        MessageResult re = new MessageResult();
        re.setCode(0);
        re.setMessage("success");
        return re;
    }

    /**
     * BTC/USDT趋势线
     *
     * @return
     */
    @RequestMapping("/btc/trend")
    public MessageResult btcTrend() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        long nowTime = calendar.getTimeInMillis();
        calendar.add(Calendar.HOUR_OF_DAY, -24);

        JSONArray array = new JSONArray();
        long firstTimeOfToday = calendar.getTimeInMillis();

        List<KLine> lines = marketService.findAllKLine("BTC/USDT", firstTimeOfToday, nowTime, "5min");
        JSONArray trend = new JSONArray();
        for (KLine line : lines) {
            trend.add(line.getClosePrice());
        }
        MessageResult re = new MessageResult();
        re.setCode(0);
        re.setData(trend);
        re.setMessage("success");
        return re;
    }

}
