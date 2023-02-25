package com.bizzan.bitrade.job;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bizzan.bitrade.coin.CoinExchangeFactory;
import com.bizzan.bitrade.util.MessageResult;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CheckExchangeRate {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CoinExchangeFactory factory;

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void syncRate() {
        log.info("CheckExchangeRate syncRate start");
        factory.getCoins().forEach(
                (symbol, value) -> {
                    String serviceName = "bitrade-market";
                    String url = "http://" + serviceName + "/market/exchange-rate/all/";
                    ResponseEntity<MessageResult> result = restTemplate.getForEntity(url+symbol, MessageResult.class);
                    log.info("remote call:url={},result={},unit={}", url, result, symbol);
                    if (result.getStatusCode().value() == 200 && result.getBody().getCode() == 0) {
                        HashMap<String,Double> ratesResult = (HashMap) result.getBody().getData();
                        HashMap<String,BigDecimal> rates = new HashMap<>();
                        for (String s : ratesResult.keySet()) {
                            rates.put(s,new BigDecimal(ratesResult.get(s).doubleValue()).setScale(2,BigDecimal.ROUND_HALF_DOWN));
                        }
                        log.info("unit = {} ,get rate success ! value = {} !", symbol, JSON.toJSON(rates));
                        factory.set(symbol, rates);
                    } else {
                        log.info("unit = {} ,get rate error ! default value zero!", symbol);
                        factory.set(symbol, new HashMap<>());
                    }
                });
        log.info("CheckExchangeRate syncRate end");
    }

}
