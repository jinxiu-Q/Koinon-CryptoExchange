package com.bizzan.bitrade.coin;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bizzan.bitrade.constant.Symbol;
import com.bizzan.bitrade.util.MessageResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

@Slf4j
public class CoinExchangeFactory {
    @Setter
    private HashMap<String,HashMap<String,BigDecimal>> coins;

    public HashMap<String,HashMap<String,BigDecimal>> getCoins(){
        return coins;
    }

    public CoinExchangeFactory(){
        coins = new HashMap<>();
    }


    /**
     * 获取币种价格
     * @param symbol
     * @return
     */
//    public BigDecimal get(String symbol){
//        return coins.get(symbol).get("CNY");
//    }

    /**
     * 获取币种价格
     * @param symbol
     * @return
     */
    public BigDecimal get(String symbol,String currency){
        return coins.get(symbol).get(currency);
    }

    public void  set(String symbol,BigDecimal rate,String currency){
        HashMap<String, BigDecimal> currencyMap = coins.get(symbol);
        if(currencyMap==null){
            currencyMap = new HashMap<>();
            coins.put(symbol,currencyMap);
        }
        currencyMap.put("CNY",rate);
        coins.put(symbol,currencyMap);
    }

    public void set(String symbol,HashMap<String, BigDecimal> currencyMap){
        coins.put(symbol,currencyMap);
        log.info("CNY:::::"+coins.get(symbol).get("CNY").toPlainString());
    }
}
