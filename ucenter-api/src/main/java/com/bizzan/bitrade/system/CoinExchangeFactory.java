package com.bizzan.bitrade.system;

import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CoinExchangeFactory {

    @Data
    public class ExchangeRate {
        public BigDecimal usdRate = BigDecimal.ONE;
        public BigDecimal cnyRate = BigDecimal.ONE;
        public BigDecimal jpyRate = BigDecimal.ONE;
        public BigDecimal eurRate = BigDecimal.ONE;
        public BigDecimal hkdRate = BigDecimal.ONE;
        public BigDecimal gbpRate = BigDecimal.ONE;
        public BigDecimal audRate = BigDecimal.ONE;
        public BigDecimal cadRate = BigDecimal.ONE;
        public BigDecimal aedRate = BigDecimal.ONE;
    }


    @Setter
    private ConcurrentHashMap<String, ExchangeRate> coins;

    public ConcurrentHashMap<String, ExchangeRate> getCoins() {
        return coins;
    }

    public CoinExchangeFactory() {
        coins = new ConcurrentHashMap<>();
    }


    /**
     * 获取币种价格
     *
     * @param symbol
     * @return
     */
    public ExchangeRate get(String symbol) {
        return coins.get(symbol);
    }

    public void set(String symbol, BigDecimal usdRate, BigDecimal cnyRate
            , BigDecimal jpyRate, BigDecimal eurRate
            , BigDecimal hkdRate, BigDecimal gbpRate
            , BigDecimal audRate, BigDecimal cadRate, BigDecimal aedRate) {
        ExchangeRate rate = new ExchangeRate();
        rate.setUsdRate(usdRate);
        rate.setCnyRate(cnyRate);
        rate.setJpyRate(jpyRate);
        rate.setEurRate(eurRate);
        rate.setHkdRate(hkdRate);
        rate.setGbpRate(gbpRate);
        rate.setAudRate(audRate);
        rate.setCadRate(cadRate);
        rate.setAedRate(aedRate);
        coins.put(symbol, rate);
    }

    public void set(String symbol, BigDecimal usdRate, BigDecimal cnyRate) {
        ExchangeRate rate = new ExchangeRate();
        rate.setUsdRate(usdRate);
        rate.setCnyRate(cnyRate);
        coins.put(symbol, rate);
    }
}
