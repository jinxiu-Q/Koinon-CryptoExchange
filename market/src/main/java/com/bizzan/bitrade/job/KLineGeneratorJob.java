package com.bizzan.bitrade.job;

import com.bizzan.bitrade.processor.CoinProcessorFactory;
import com.bizzan.bitrade.service.ExchangeCoinService;
import com.bizzan.bitrade.service.KlineRobotMarketService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.WebSocketConnectionManage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * 生成各时间段的K线信息
 *
 */
@Component
@Slf4j
public class KLineGeneratorJob {
    @Autowired
    private CoinProcessorFactory processorFactory;
    @Autowired
    private ExchangeCoinService coinService;


	@Autowired
	private TaskExecutor taskExecutor;
	@Autowired
	private KlineRobotMarketService klineRobotMarketService;

	public static String PERIOD[] ={ "1min", "5min", "15min", "30min", "60min", "1day", "1mon", "1week" };

//    /**
//     * 每分钟定时器，处理分钟K线
//     */
//    @Scheduled(cron = "0 * * * * *")
//    public void handle5minKLine(){
//        Calendar calendar = Calendar.getInstance();
//        log.info("分钟K线:{}",calendar.getTime());
//        //将秒、微秒字段置为0
//        calendar.set(Calendar.SECOND,0);
//        calendar.set(Calendar.MILLISECOND,0);
//        long time = calendar.getTimeInMillis();
//        int minute = calendar.get(Calendar.MINUTE);
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        processorFactory.getProcessorMap().forEach((symbol,processor)->{
//        	if(!processor.isStopKline()) {
//				taskExecutor.execute(new Runnable() {
//					@Override
//					public void run() {
//						processor.generateKLine(time, minute, hour);
//					}
//				});
//			}
//        });
//    }

	/**
     * 每小时运行
     */
    @Scheduled(cron = "0 0 * * * *")
    public void handleHourKLine(){
        processorFactory.getProcessorMap().forEach((symbol,processor)-> {
        	if(!processor.isStopKline()) {
	            Calendar calendar = Calendar.getInstance();
	            log.info("小时K线:{}",calendar.getTime());
	            //将秒、微秒字段置为0
	            calendar.set(Calendar.MINUTE, 0);
	            calendar.set(Calendar.SECOND, 0);
	            calendar.set(Calendar.MILLISECOND, 0);
	            long time = calendar.getTimeInMillis();

	            processor.generateKLine(1, Calendar.HOUR_OF_DAY, time);
        	}
        });
    }

    /**
     * 每日0点处理器，处理日K线
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void handleDayKLine(){
        processorFactory.getProcessorMap().forEach((symbol,processor)->{
        	if(!processor.isStopKline()) {
	            Calendar calendar = Calendar.getInstance();
	            log.info("日K线:{}",calendar.getTime());
	            //将秒、微秒字段置为0
	            calendar.set(Calendar.HOUR_OF_DAY,0);
	            calendar.set(Calendar.MINUTE,0);
	            calendar.set(Calendar.SECOND,0);
	            calendar.set(Calendar.MILLISECOND,0);
	            long time = calendar.getTimeInMillis();
	            int week = calendar.get(Calendar.DAY_OF_WEEK);
	            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
	            if(week == 1){
	                processor.generateKLine(1, Calendar.DAY_OF_WEEK, time);
	            }
	            if(dayOfMonth == 1){
	                processor.generateKLine(1, Calendar.DAY_OF_MONTH, time);
	            }
	            processor.generateKLine(1, Calendar.DAY_OF_YEAR,time);
        	}
        });
    }

	/**
	 * 每分钟定时器，处理分钟K线
	 */
//	@Scheduled(cron = "0 32 * * * *")
//	public void handle5minKLine(){
//		syncKLine("BTC/USDT");
////		processorFactory.getProcessorMap().forEach((symbol,processor)->{
////			if(!processor.isStopKline()) {
////				taskExecutor.execute(new Runnable() {
////					@Override
////					public void run() {
////						syncKLine(symbol);
////					}
////				});
////			}
////		});
//	}


	public void syncKLine(String symbol){
		klineRobotMarketService.deleteAll(symbol);
		// 获取当前时间(秒)
		Long currentTime = DateUtil.getTimeMillis() / 1000;
		// 初始化K线，时间点
//        int count = 2000;
		log.info("分钟执行获取K线[Start]");
		for(String period : PERIOD) {
//			long fromTime = 0;
			long fromTime = klineRobotMarketService.findMaxTimestamp(symbol,period); // +1是为了不获取上一次获取的最后一条K线
			if(fromTime<=1){
				fromTime = 0;
			}else {
				fromTime = fromTime/1000;
			}
			long timeGap = currentTime - fromTime;
			log.info(symbol+"分钟K线currentTime:{},fromTime:{},timeGap:{}",currentTime,fromTime,timeGap);
			if(period.equals("1min") && timeGap >= 60 ) { // 超出1分钟
				if(fromTime == 0) {
					//logger.info("分钟执行获取K线[1min] ===> from == 0");
					// 初始化K线，获取最近600根K线
					WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, currentTime - 60 * 2500, currentTime);
				}else{
					// 非初始化，获取最近产生的K线
					long toTime = fromTime + (timeGap / 60) * 60 - 5;//timeGap - (timeGap % 60); // +10秒是为了获取本区间内的K线
					WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, fromTime, toTime);
				}
			}

			if(period.equals("5min") && timeGap >= 60 * 5 ) { // 超出5分钟
				if(fromTime == 0) {
					// 初始化K线，获取最近600根K线
					WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, currentTime - 5 * 60 * 1000, currentTime);
				}else{
					// 非初始化，获取最近产生的K线
					long toTime = fromTime + (timeGap / (60 * 5)) * (60*5) - 5;//timeGap - (timeGap % 60); // +10秒是为了获取本区间内的K线
					WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, fromTime, toTime);
				}
			}

			if(period.equals("15min") && timeGap >= (60 * 15 )) { // 超出15分钟
				if(fromTime == 0) {
					// 初始化K线，获取最近600根K线
					WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, currentTime - 15 * 60 * 1000, currentTime);
				}else {
					// 非初始化，获取最近产生的K线
					long toTime = fromTime + (timeGap / (60 * 15)) * (60 * 15) - 5;//timeGap - (timeGap % 60); // +10秒是为了获取本区间内的K线
					WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, fromTime, toTime);
				}
			}

			if(period.equals("30min") && timeGap >= (60 * 30 )) { // 超出30分钟
				if(fromTime == 0) {
					// 初始化K线，获取最近600根K线
					WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, currentTime - 30 * 60 * 1000, currentTime);
				}else{
					long toTime = fromTime + (timeGap / (60 * 30)) * (60 * 30) - 5;//timeGap - (timeGap % 60); // +10秒是为了获取本区间内的K线
					WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, fromTime, toTime);
				}
			}

			if(period.equals("60min") && timeGap >= (60 * 60 )) { // 超出60分钟
				if(fromTime == 0) {
					// 初始化K线，获取最近600根K线
					WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, currentTime - 60 * 60 * 1000, currentTime);
				}else{
					long toTime = fromTime + (timeGap / (60 * 60)) * (60 * 60) - 5;//timeGap - (timeGap % 60); // +10秒是为了获取本区间内的K线
					WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, fromTime, toTime);
				}
			}

			if(period.equals("4hour") && timeGap >= (60 * 60 * 4 )) { // 超出4小时
				if(fromTime == 0) {
					// 初始化K线，获取最近600根K线
					WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, currentTime - 4 * 60 * 60 * 600, currentTime);
				}else{
					long toTime = fromTime + (timeGap / (60 * 60 * 4)) * (60 * 60 * 4) - 5;//timeGap - (timeGap % 60); // +10秒是为了获取本区间内的K线
					WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, fromTime, toTime);
				}
			}

			if(period.equals("1day") && timeGap >= (60 * 60 * 24)) { // 超出24小时
				if(fromTime == 0) {
					// 初始化K线，获取最近600根K线
					WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, currentTime - 24 * 60 * 60 * 1000, currentTime);
				}else {
					long toTime = fromTime + (timeGap / (60 * 60 * 24)) * (60 * 60 * 24) - 5;//timeGap - (timeGap % 60); // +10秒是为了获取本区间内的K线
					WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, fromTime, toTime);
				}
			}

			if(period.equals("1week") && timeGap >= (60 * 60 * 24 * 7)) { // 超出24小时
				if(fromTime == 0) {
					// 初始化K线，获取最近600根K线
					WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, currentTime - 7 * 24 * 60 * 60 * 500, currentTime);
				}else{
					long toTime = fromTime + (timeGap / (60 * 60 * 24*7)) * (60 * 60 * 24*7) - 5;//timeGap - (timeGap % 60); // +10秒是为了获取本区间内的K线
					WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, fromTime, toTime);
				}
			}

			if(period.equals("1mon") && timeGap >= (60 * 60 * 24 * 30)) { // 超出24小时
				if(fromTime == 0) {
					// 初始化K线，获取最近600根K线
					WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, currentTime - 30 * 24 * 60 * 60 * 100, currentTime);
				}else{
					long toTime = fromTime + (timeGap / (60 * 60 * 24 * 30)) * (60 * 60 * 24 * 30) - 5;//timeGap - (timeGap % 60); // +10秒是为了获取本区间内的K线
					WebSocketConnectionManage.getWebSocket().reqKLineList(symbol, period, fromTime, toTime);
				}
			}
		}
	}
}
