package com.bizzan.bitrade.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bizzan.bitrade.component.CoinExchangeRate;
import com.bizzan.bitrade.util.MessageResult;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/exchange-rate")
public class ExchangeRateController {
    @Autowired
    private CoinExchangeRate coinExchangeRate;

    @RequestMapping("usd/{coin}")
    public MessageResult getUsdExchangeRate(@PathVariable String coin) {
        MessageResult mr = new MessageResult(0, "success");
        BigDecimal latestPrice = coinExchangeRate.getUsdRate(coin);
        mr.setData(latestPrice.toString());
        return mr;
    }

//    @RequestMapping("usdtcny")
//    public MessageResult getUsdtExchangeRate(){
//        MessageResult mr = new MessageResult(0,"success");
//        BigDecimal latestPrice = coinExchangeRate.getUsdCnyRate();
//        mr.setData(latestPrice.toString());
//        return mr;
//    }
//
    @RequestMapping("all/{coin}")
    public MessageResult getAllExchangeRate(@PathVariable String coin){
        MessageResult mr = new MessageResult(0,"success");
        Map<String,BigDecimal> ratesMap = coinExchangeRate.getAllRate(coin);
        mr.setData(ratesMap);
        return mr;
    }
//
//    @RequestMapping("cny/{coin}")
//    public MessageResult getCnyExchangeRate(@PathVariable String coin){
//        MessageResult mr = new MessageResult(0,"success");
//        BigDecimal latestPrice = coinExchangeRate.getCnyRate(coin);
//        mr.setData(latestPrice.toString());
//        return mr;
//    }
//
//    @RequestMapping("jpy/{coin}")
//    public MessageResult getJpyExchangeRate(@PathVariable String coin){
//        MessageResult mr = new MessageResult(0,"success");
//        BigDecimal latestPrice = coinExchangeRate.getJpyRate(coin);
//        mr.setData(latestPrice.toString());
//        return mr;
//    }
//
//    @RequestMapping("hkd/{coin}")
//    public MessageResult getHkdExchangeRate(@PathVariable String coin){
//        MessageResult mr = new MessageResult(0,"success");
//        BigDecimal latestPrice = coinExchangeRate.getHkdRate(coin);
//        mr.setData(latestPrice.toString());
//        return mr;
//    }

    @RequestMapping("usd-{unit}")
    public MessageResult getUsdCurrenciesRate(@PathVariable String unit) {
        MessageResult mr = new MessageResult(0, "success");
        if ("CNY".equalsIgnoreCase(unit)) {
            mr.setData(coinExchangeRate.getUsdCnyRate());
        } else if ("JPY".equalsIgnoreCase(unit)) {
            mr.setData(coinExchangeRate.getUsdJpyRate());
        } else if ("EUR".equalsIgnoreCase(unit)) {
            mr.setData(coinExchangeRate.getUsdEurRate());
        } else if ("HKD".equalsIgnoreCase(unit)) {
            mr.setData(coinExchangeRate.getUsdHkdRate());
        } else if ("GBP".equalsIgnoreCase(unit)) {
            mr.setData(coinExchangeRate.getUsdGbpRate());
        } else if ("AUD".equalsIgnoreCase(unit)) {
            mr.setData(coinExchangeRate.getUsdAudRate());
        }  else if ("CAD".equalsIgnoreCase(unit)) {
            mr.setData(coinExchangeRate.getUsdCadRate());
        } else if ("AED".equalsIgnoreCase(unit)) {
            mr.setData(coinExchangeRate.getUsdAedRate());
        } else {
            mr.setData(BigDecimal.ZERO);
        }
        return mr;
    }
}
