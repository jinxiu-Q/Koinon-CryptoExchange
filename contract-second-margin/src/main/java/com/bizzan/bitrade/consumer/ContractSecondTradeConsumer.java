package com.bizzan.bitrade.consumer;

import com.alibaba.fastjson.JSON;
import com.bizzan.bitrade.entity.ExchangeTrade;
import com.bizzan.bitrade.processor.ContractProcessor;
import com.bizzan.bitrade.processor.ContractProcessorFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ContractSecondTradeConsumer {

    private Logger logger = LoggerFactory.getLogger(ContractSecondTradeConsumer.class);
    @Autowired
    private ContractProcessorFactory contractProcessorFactory;

    private ExecutorService executor = new ThreadPoolExecutor(30, 100, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), new ThreadPoolExecutor.AbortPolicy());

    /**
     * 接收来自交易模块的成交信息，确定最新价格
     *
     * @param records
     */
    @KafkaListener(topics = "contract-second-trade", containerFactory = "kafkaListenerContainerFactory")
    public void handleTrade(List<ConsumerRecord<String, String>> records) {
        for (int i = 0; i < records.size(); i++) {
            ConsumerRecord<String, String> record = records.get(i);
            executor.submit(new HandleTradeThread(record));
        }
    }

    public class HandleTradeThread implements Runnable {
        private ConsumerRecord<String, String> record;

        private HandleTradeThread(ConsumerRecord<String, String> record) {
            this.record = record;
        }

        @Override
        public void run() {
            try {
                List<ExchangeTrade> trades = JSON.parseArray(record.value(), ExchangeTrade.class);
                String symbol = trades.get(0).getSymbol();
                ContractProcessor processor = contractProcessorFactory.getProcessor(symbol);
                for (ExchangeTrade trade : trades) {
                    processor.setCurPrice(trade.getPrice());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
