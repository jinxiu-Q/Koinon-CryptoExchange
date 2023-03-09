package com.bizzan.bitrade.config;

import com.bizzan.bitrade.entity.ExchangeCoin;
import com.bizzan.bitrade.processor.ContractProcessorFactory;
import com.bizzan.bitrade.processor.DefaultContractProcessor;
import com.bizzan.bitrade.service.ExchangeCoinService;
import com.bizzan.bitrade.service.ExchangeOrderCircleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@Slf4j
public class ProcessorConfig {

    @Autowired
    private ExchangeOrderCircleService ordercricleService;
    @Bean
    public ContractProcessorFactory processorFactory(
            ExchangeCoinService coinService,
            RestTemplate restTemplate) {

        log.info("====initialized ContractProcessorFactory start==================================");
        ContractProcessorFactory factory = new ContractProcessorFactory();
        List<ExchangeCoin> coins = coinService.findAllEnabled();
        log.info("exchange-coin result:{}", coins);

        for (ExchangeCoin coin : coins) {
            DefaultContractProcessor processor = new DefaultContractProcessor(coin.getSymbol());
//            processor.addHandler(mongoMarketHandler);
//            processor.addHandler(wsHandler);
//            processor.addHandler(nettyHandler);
            processor.setOrderCircleService(ordercricleService);
//            processor.setExchangeRate(exchangeRate);
//            processor.setIsStopKLine(true);
            factory.addProcessor(coin.getSymbol(), processor);
            log.info("new processor = ", processor);
        }

        log.info("====initialized ContractProcessorFactory completed====");
        log.info("ContractProcessorFactory = ", factory);
        return factory;
    }
}
