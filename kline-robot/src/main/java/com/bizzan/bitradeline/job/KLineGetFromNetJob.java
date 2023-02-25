package com.bizzan.bitradeline.job;

import com.bizzan.bitradeline.service.KlineRobotMarketService;
import com.bizzan.bitradeline.util.WebSocketConnectionManage;
import com.bizzan.bitradeline.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 生成各时间段的K线信息
 */
@Component
@Slf4j
public class KLineGetFromNetJob {
    @Autowired
    private KlineRobotMarketService klineRobotMarketService;

    public static String PERIOD[] = {"1min", "5min", "15min", "30min", "60min", "1day", "1mon", "1week"};

    public static String symbolArray[] = {
            "BTC/USDT", "BSV/USDT", "BCH/USDT", "DAO/USDT", "DASH/USDT", "DOGE/USDT", "EOS/ETH", "EOS/USDT", "ETC/USDT", "ETH/BTC",
            "ETH/USDT", "HT/USDT", "LTC/USDT", "NEO/USDT", "TRX/BTC", "TRX/ETH", "TRX/USDT", "XRP/BTC", "XRP/USDT", "YFI/USDT", "YFII/USDT"};


    /**
     * 每分钟定时器，处理分钟K线
     */
    @Scheduled(cron = "0/30 * * * * *")
    public void handle5minKLine() {
        for (int index = 0; index < symbolArray.length; index++) {
            syncKLine(symbolArray[index]);
        }
    }


    @Scheduled(cron = "0 1/5 * * * *")
    public void handle5minKLine60() {
        for (int index = 0; index < symbolArray.length; index++) {
            syncKLine60(symbolArray[index]);
        }
    }

    public void syncKLine(String symbol) {
//        klineRobotMarketService.deleteAll(symbol);
        // 获取当前时间(秒)
        Long currentTime = DateUtil.getTimeMillis() / 1000;
        // 初始化K线，时间点
        log.info("分钟执行获取K线[Start]");
        for (String period : PERIOD) {
            long fromTime = klineRobotMarketService.findMaxTimestamp(symbol, period); // +1是为了不获取上一次获取的最后一条K线
            if (fromTime <= 1) {
                fromTime = 0;
            } else {
                fromTime = fromTime / 1000;
            }
            long timeGap = currentTime - fromTime;
            log.info(symbol + "分钟K线currentTime:{},fromTime:{},timeGap:{}", currentTime, fromTime, timeGap);
            if (period.equals("1min") && timeGap >= 60) { // 超出1分钟
                if (fromTime == 0) {
                    // 初始化K线，获取最近600根K线
                    WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, currentTime - 60 * 2500, currentTime);
                } else {
                    // 非初始化，获取最近产生的K线
                    long toTime = fromTime + (timeGap / 60) * 60 - 5;//timeGap - (timeGap % 60); // +10秒是为了获取本区间内的K线
                    WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, fromTime, toTime);
                }
            }

            if (period.equals("5min") && timeGap >= 60 * 5) { // 超出5分钟
                if (fromTime == 0) {
                    // 初始化K线，获取最近600根K线
                    WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, currentTime - 5 * 60 * 1000, currentTime);
                } else {
                    // 非初始化，获取最近产生的K线
                    long toTime = fromTime + (timeGap / (60 * 5)) * (60 * 5) - 5;//timeGap - (timeGap % 60); // +10秒是为了获取本区间内的K线
                    WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, fromTime, toTime);
                }
            }

            if (period.equals("15min") && timeGap >= (60 * 15)) { // 超出15分钟
                if (fromTime == 0) {
                    // 初始化K线，获取最近600根K线
                    WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, currentTime - 15 * 60 * 1000, currentTime);
                } else {
                    // 非初始化，获取最近产生的K线
                    long toTime = fromTime + (timeGap / (60 * 15)) * (60 * 15) - 5;//timeGap - (timeGap % 60); // +10秒是为了获取本区间内的K线
                    WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, fromTime, toTime);
                }
            }

            if (period.equals("30min") && timeGap >= (60 * 30)) { // 超出30分钟
                if (fromTime == 0) {
                    // 初始化K线，获取最近600根K线
                    WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, currentTime - 30 * 60 * 1000, currentTime);
                } else {
                    long toTime = fromTime + (timeGap / (60 * 30)) * (60 * 30) - 5;//timeGap - (timeGap % 60); // +10秒是为了获取本区间内的K线
                    WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, fromTime, toTime);
                }
            }
        }
    }


    public void syncKLine60(String symbol) {
        // 获取当前时间(秒)
        Long currentTime = DateUtil.getTimeMillis() / 1000;
        // 初始化K线，时间点
        log.info("分钟执行获取K线[Start]");
        for (String period : PERIOD) {
            long fromTime = klineRobotMarketService.findMaxTimestamp(symbol, period); // +1是为了不获取上一次获取的最后一条K线
            if (fromTime <= 1) {
                fromTime = 0;
            } else {
                fromTime = fromTime / 1000;
            }
            long timeGap = currentTime - fromTime;
            log.info(symbol + "分钟K线currentTime:{},fromTime:{},timeGap:{}", currentTime, fromTime, timeGap);

            if (period.equals("60min") && timeGap >= (60 * 60)) { // 超出60分钟
                if (fromTime == 0) {
                    // 初始化K线，获取最近600根K线
                    WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, currentTime - 60 * 60 * 1000, currentTime);
                } else {
                    long toTime = fromTime + (timeGap / (60 * 60)) * (60 * 60) - 5;//timeGap - (timeGap % 60); // +10秒是为了获取本区间内的K线
                    WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, fromTime, toTime);
                }
            }

            if (period.equals("4hour") && timeGap >= (60 * 60 * 4)) { // 超出4小时
                if (fromTime == 0) {
                    // 初始化K线，获取最近600根K线
                    WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, currentTime - 4 * 60 * 60 * 1000, currentTime);
                } else {
                    long toTime = fromTime + (timeGap / (60 * 60 * 4)) * (60 * 60 * 4) - 5;//timeGap - (timeGap % 60); // +10秒是为了获取本区间内的K线
                    WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, fromTime, toTime);
                }
            }

            if (period.equals("1day") && timeGap >= (60 * 60 * 24)) { // 超出24小时
                if (fromTime == 0) {
                    // 初始化K线，获取最近600根K线
                    WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, currentTime - 24 * 60 * 60 * 1000, currentTime);
                } else {
                    long toTime = fromTime + (timeGap / (60 * 60 * 24)) * (60 * 60 * 24) - 5;//timeGap - (timeGap % 60); // +10秒是为了获取本区间内的K线
                    WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, fromTime, toTime);
                }
            }

            if (period.equals("1week") && timeGap >= (60 * 60 * 24 * 7)) { // 超出24小时
                if (fromTime == 0) {
                    // 初始化K线，获取最近600根K线
                    WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, currentTime - 7 * 24 * 60 * 60 * 500, currentTime);
                } else {
                    long toTime = fromTime + (timeGap / (60 * 60 * 24 * 7)) * (60 * 60 * 24 * 7) - 5;//timeGap - (timeGap % 60); // +10秒是为了获取本区间内的K线
                    WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, fromTime, toTime);
                }
            }

            if (period.equals("1mon") && timeGap >= (60 * 60 * 24 * 30)) { // 超出24小时
                if (fromTime == 0) {
                    // 初始化K线，获取最近600根K线
                    WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, currentTime - 30 * 24 * 60 * 60 * 100, currentTime);
                } else {
                    long toTime = fromTime + (timeGap / (60 * 60 * 24 * 30)) * (60 * 60 * 24 * 30) - 5;//timeGap - (timeGap % 60); // +10秒是为了获取本区间内的K线
                    WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, fromTime, toTime);
                }
            }
        }
    }
}
