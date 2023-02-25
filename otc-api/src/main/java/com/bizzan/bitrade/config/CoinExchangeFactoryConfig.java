package com.bizzan.bitrade.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bizzan.bitrade.coin.CoinExchangeFactory;
import com.bizzan.bitrade.entity.OtcCoin;
import com.bizzan.bitrade.service.OtcCoinService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class CoinExchangeFactoryConfig {

    @Bean
    public CoinExchangeFactory getCoinExchangeFactory(OtcCoinService coinService) {
        log.info("init CoinExchangeFactory start");
        List<OtcCoin> coins = coinService.findAll();
        CoinExchangeFactory factory = new CoinExchangeFactory();
        HashMap<String,BigDecimal> ratesMap = new HashMap<String,BigDecimal>(){{
            put("CNY",new BigDecimal(0));
            put("TWD",new BigDecimal(0));
            put("USD",new BigDecimal(0));
            put("EUR",new BigDecimal(0));
            put("HKD",new BigDecimal(0));
            put("SGD",new BigDecimal(0));
        }};
        coins.forEach(coin -> {
            factory.set(coin.getUnit(), ratesMap);
            log.info("factory add unit = {} , rate  = 0 !", coin.getUnit());
        });
        factory.set("USDT", ratesMap);
        log.info("factory add unit = USDT , rate  = 0!");
        log.info("init CoinExchangeFactory end");
        return factory;
    }
}
