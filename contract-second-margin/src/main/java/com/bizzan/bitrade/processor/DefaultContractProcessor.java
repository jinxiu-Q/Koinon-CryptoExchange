package com.bizzan.bitrade.processor;

import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.service.ExchangeOrderCircleService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DefaultContractProcessor implements ContractProcessor {
    private Logger logger = LoggerFactory.getLogger(DefaultContractProcessor.class);
    private String symbol;

    public String getSymbol() {
        return symbol;
    }

    private BigDecimal curPrice=new BigDecimal(1);

    //按结束时间排序的等待处理的秒合约
    private TreeMap<Long, MergeSecondOrder> waitingQueue;

    private ExchangeOrderCircleService ordercircleService;

    private ExecutorService executor = new ThreadPoolExecutor(30, 100, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), new ThreadPoolExecutor.AbortPolicy());

    public DefaultContractProcessor(String symbol) {
        this.symbol = symbol;
        this.waitingQueue = new TreeMap<>(Comparator.naturalOrder());
    }

    public void setOrderCircleService(ExchangeOrderCircleService t){
        this.ordercircleService = t;
        //重启时数据库中状态为0的全部拉回来处理
        List<ExchangeOrderCricle> list = ordercircleService.queryExchangeOrderCircleStatusTrading();
        Iterator<ExchangeOrderCricle> it = list.iterator();
        while (it.hasNext()) {
            ExchangeOrderCricle s = it.next();
            addExchangeOrderCircle(s);
        }
    }
    @Override
    public void setCurPrice(BigDecimal price) {
        log.info("DefaultContractProcessor setCurPrice symbol = {},price={}", symbol, price);
        this.curPrice = price;
    }

    @Override
    public BigDecimal getCurPrice() {
        return curPrice;
    }

    @Override
    public void addExchangeOrderCircle(ExchangeOrderCricle order) {
        Long createTime = order.getTime();
        ExchangeOrderCirclePeriod period = order.getPeriod();
        Long time = createTime.longValue() / 1000 + period.getCode();
        time = time * 1000;
        order.setCompletedTime(time);
        ///////
        synchronized (waitingQueue) {
            MergeSecondOrder mergeOrder = waitingQueue.get(order.getCompletedTime());
            if (mergeOrder == null) {
                mergeOrder = new MergeSecondOrder();
                mergeOrder.add(order);
                waitingQueue.put(order.getCompletedTime(), mergeOrder);
            } else {
                mergeOrder.add(order);
            }
        }
    }

    @Override
    public void cancelExchangeOrderCircle(ExchangeOrderCricle order) {
        Long createTime = order.getTime();
        ExchangeOrderCirclePeriod period = order.getPeriod();
        Long time = createTime.longValue() / 1000 + period.getCode();
        time = time * 1000;
        order.setCompletedTime(time);
        synchronized (waitingQueue) {
            MergeSecondOrder mergeOrder = waitingQueue.get(order.getCompletedTime());
            if (mergeOrder != null) {
                Iterator<ExchangeOrderCricle> it = mergeOrder.iterator();
                while (it.hasNext()) {
                    ExchangeOrderCricle s = it.next();
                    if (s.getOrderId().equalsIgnoreCase(order.getOrderId())) {
                        mergeOrder.remove(order);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void handle(long curTime) {
        if (waitingQueue.size() > 0) {
            Map.Entry<Long, MergeSecondOrder> mapEntry = waitingQueue.firstEntry();
            long timeObj = mapEntry.getKey();
            //本时间之前的全部取消
            while (timeObj < curTime) {
                MergeSecondOrder mergeOrder = waitingQueue.remove(timeObj);
                executor.submit(new DefaultContractProcessor.HandleTradeThread(mergeOrder, curPrice, 0));
                if (waitingQueue.size() > 0) {
                    Map.Entry<Long, MergeSecondOrder> mapEntryFirst = waitingQueue.firstEntry();
                    timeObj = mapEntryFirst.getKey();
                } else {
                    break;
                }
            }
        }
        //当前时间的执行
        MergeSecondOrder mergeOrder = null;
        synchronized (waitingQueue) {
            mergeOrder = waitingQueue.remove(curTime);
        }
        if (mergeOrder != null) {
            executor.submit(new DefaultContractProcessor.HandleTradeThread(mergeOrder, curPrice, 1));
        }
    }


    public class HandleTradeThread implements Runnable {
        private MergeSecondOrder mergeSecondOrder;
        private BigDecimal price;

        private int flag;

        private HandleTradeThread(MergeSecondOrder record, BigDecimal price, int flag) {
            this.mergeSecondOrder = record;
            this.price = price;
            this.flag = flag;
        }

        @Override
        public void run() {
            if (flag == 0) {
                cancelTimeOver();
            } else {
                handleThisTime();
            }
        }

        /*取消过时的订单*/
        private void cancelTimeOver() {
            Iterator<ExchangeOrderCricle> it = mergeSecondOrder.iterator();
            while (it.hasNext()) {
                ExchangeOrderCricle s = it.next();
                ordercircleService.cancelOrder(s.getOrderId());
            }
        }

        /*处理当前时间结束的订单*/
        private void handleThisTime() {
            Iterator<ExchangeOrderCricle> it = mergeSecondOrder.iterator();
            while (it.hasNext()) {
                ExchangeOrderCricle s = it.next();
                ordercircleService.completedOrder(s.getOrderId(), price);
            }
        }
    }

}
