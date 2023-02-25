package com.bizzan.bitrade.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.CoinThumb;
import com.bizzan.bitrade.processor.CoinProcessor;
import com.bizzan.bitrade.processor.CoinProcessorFactory;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.ExchangeCoinService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 币种汇率管理
 */
@Component
@Slf4j
@ToString
public class CoinExchangeRate {
    @Getter
    @Setter
    private BigDecimal usdCnyRate = new BigDecimal("6.45");
    @Getter
    @Setter
    private BigDecimal usdJpyRate = new BigDecimal("110.02");
    @Getter
    @Setter
    private BigDecimal usdHkdRate = new BigDecimal("7.8491");
    @Getter
    @Setter
    private BigDecimal usdEurRate = new BigDecimal("5.08");
    @Getter
    @Setter
    private BigDecimal usdGbpRate = new BigDecimal("5.08");
    @Getter
    @Setter
    private BigDecimal usdCadRate = new BigDecimal("5.08");
    @Getter
    @Setter
    private BigDecimal usdChfRate = new BigDecimal("5.08");
    @Getter
    @Setter
    private BigDecimal usdAudRate = new BigDecimal("5.08");
    @Getter
    @Setter
    private BigDecimal usdAedRate = new BigDecimal("5.08");
    @Setter
    private CoinProcessorFactory coinProcessorFactory;
    private Map<String, BigDecimal> ratesMap = new HashMap<String, BigDecimal>() {{
        put("CNY", new BigDecimal("6.36"));
        put("JPY", new BigDecimal("6.40"));
        put("USD", new BigDecimal("1.00"));
        put("EUR", new BigDecimal("0.91"));
        put("HKD", new BigDecimal("7.81"));
        put("GBP", new BigDecimal("1.36"));
        put("AUD", new BigDecimal("0.91"));
        put("CAD", new BigDecimal("7.81"));
        put("AED", new BigDecimal("1.36"));
    }};

    @Autowired
    private CoinService coinService;
    @Autowired
    private ExchangeCoinService exCoinService;


    public BigDecimal getUsdRate(String symbol) {
        //log.info("CoinExchangeRate getUsdRate unit = " + symbol);
        if ("USDT".equalsIgnoreCase(symbol)) {
            return BigDecimal.ONE;
        } else if ("CNY".equalsIgnoreCase(symbol)) {
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdCnyRate, 4, BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        } else if ("JPY".equalsIgnoreCase(symbol)) {
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdJpyRate, 4, BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        } else if ("HKD".equalsIgnoreCase(symbol)) {
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdHkdRate, 4, BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        }else if ("AED".equalsIgnoreCase(symbol)) {
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdAedRate, 4, BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        } else if ("CAD".equalsIgnoreCase(symbol)) {
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdCadRate, 4, BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        } else if ("AUD".equalsIgnoreCase(symbol)) {
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdAudRate, 4, BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        }else if ("GBP".equalsIgnoreCase(symbol)) {
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdGbpRate, 4, BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        } else if ("EUR".equalsIgnoreCase(symbol)) {
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdEurRate, 4, BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        }
        String usdtSymbol = symbol.toUpperCase() + "/USDT";
        String btcSymbol = symbol.toUpperCase() + "/BTC";
        String ethSymbol = symbol.toUpperCase() + "/ETH";

        if (coinProcessorFactory != null) {
            if (coinProcessorFactory.containsProcessor(usdtSymbol)) {
                log.info("Support exchange coin = {}", usdtSymbol);
                CoinProcessor processor = coinProcessorFactory.getProcessor(usdtSymbol);
                if (processor == null) {
                    return BigDecimal.ZERO;
                }
                CoinThumb thumb = processor.getThumb();
                if (thumb == null) {
                    log.info("Support exchange coin thumb is null", thumb);
                    return BigDecimal.ZERO;
                }
                return thumb.getUsdRate();
            } else if (coinProcessorFactory.containsProcessor(btcSymbol)) {
                log.info("Support exchange coin = {}/BTC", btcSymbol);
                CoinProcessor processor = coinProcessorFactory.getProcessor(btcSymbol);
                if (processor == null) {
                    return BigDecimal.ZERO;
                }
                CoinThumb thumb = processor.getThumb();
                if (thumb == null) {
                    log.info("Support exchange coin thumb is null", thumb);
                    return BigDecimal.ZERO;
                }
                return thumb.getUsdRate();
            } else if (coinProcessorFactory.containsProcessor(ethSymbol)) {
                log.info("Support exchange coin = {}/ETH", ethSymbol);
                CoinProcessor processor = coinProcessorFactory.getProcessor(ethSymbol);
                if (processor == null) {
                    return BigDecimal.ZERO;
                }
                CoinThumb thumb = processor.getThumb();
                if (thumb == null) {
                    log.info("Support exchange coin thumb is null", thumb);
                    return BigDecimal.ZERO;
                }
                return thumb.getUsdRate();
            } else {
                return getDefaultUsdRate(symbol);
            }
        } else {
            return getDefaultUsdRate(symbol);
        }
    }

    /**
     * 获取币种设置里的默认价格
     *
     * @param symbol
     * @return
     */
    public BigDecimal getDefaultUsdRate(String symbol) {
        Coin coin = coinService.findByUnit(symbol);
        if (coin != null) {
            return new BigDecimal(coin.getUsdRate());
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getCnyRate(String symbol) {
        if ("CNY".equalsIgnoreCase(symbol)) {
            return BigDecimal.ONE;
        }
        return getUsdRate(symbol).multiply(usdCnyRate).setScale(2, RoundingMode.DOWN);
    }


    public BigDecimal getJpyRate(String symbol) {
        if ("JPY".equalsIgnoreCase(symbol)) {
            return BigDecimal.ONE;
        }
        return getUsdRate(symbol).multiply(usdJpyRate).setScale(2, RoundingMode.DOWN);
    }

    public BigDecimal getHkdRate(String symbol) {
        if ("HKD".equalsIgnoreCase(symbol)) {
            return BigDecimal.ONE;
        }
        return getUsdRate(symbol).multiply(usdHkdRate).setScale(2, RoundingMode.DOWN);
    }
    public BigDecimal getEurRate(String symbol) {
        if ("Eur".equalsIgnoreCase(symbol)) {
            return BigDecimal.ONE;
        }
        return getUsdRate(symbol).multiply(usdEurRate).setScale(2, RoundingMode.DOWN);
    }
    public BigDecimal getGbpRate(String symbol) {
        if ("GBP".equalsIgnoreCase(symbol)) {
            return BigDecimal.ONE;
        }
        return getUsdRate(symbol).multiply(usdGbpRate).setScale(2, RoundingMode.DOWN);
    }

    public BigDecimal getCadRate(String symbol) {
        if ("CAD".equalsIgnoreCase(symbol)) {
            return BigDecimal.ONE;
        }
        return getUsdRate(symbol).multiply(usdCadRate).setScale(2, RoundingMode.DOWN);
    }
    public BigDecimal getChfRate(String symbol) {
        if ("CHF".equalsIgnoreCase(symbol)) {
            return BigDecimal.ONE;
        }
        return getUsdRate(symbol).multiply(usdChfRate).setScale(2, RoundingMode.DOWN);
    }

    public BigDecimal getAudRate(String symbol) {
        if ("AUD".equalsIgnoreCase(symbol)) {
            return BigDecimal.ONE;
        }
        return getUsdRate(symbol).multiply(usdAudRate).setScale(2, RoundingMode.DOWN);
    }


//    //
//    public static void main(String[] args) {
//        CoinExchangeRate rate = new CoinExchangeRate();
//        try {
//            rate.syncCurrenciesPrice();
//        } catch (UnirestException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Scheduled(cron = "0 */5 * * * *")
    public void syncCurrenciesPrice() throws UnirestException {
        String url = "https://tradingeconomics.com/currencies?base=usd";
        Pattern p_html = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE);
        Pattern p_trn = Pattern.compile("\\s*|\t|\r|\n");
        int startindex = 1, endindex;
        //如有报错 请自行官网申请获取汇率 或者默认写死
        HttpResponse<String> resp = Unirest.get(url).asString();
        if (resp.getStatus() == 200) { //正确返回
            String body = resp.getBody();
            if (body.length() > 130000) {
                body = body.substring(130000);
            }
            startindex = body.indexOf("datatable-row");
            while (startindex > -1) {
                endindex = body.indexOf("</tr>", startindex);
                String cellhtml = "<" + body.substring(startindex, endindex);
                cellhtml = cellhtml.replace("</td>", ",");
                Matcher m_html = p_html.matcher(cellhtml);
                cellhtml = m_html.replaceAll(""); //过滤html标签
                Matcher m = p_trn.matcher(cellhtml);
                cellhtml = m.replaceAll("");
                //
                String symbol = cellhtml.split(",")[0];
                String price = cellhtml.split(",")[1];

                if ("USDCNY".equals(symbol)) {
                    setUsdCnyRate(new BigDecimal(price).setScale(2, RoundingMode.DOWN));
                } else if ("USDJPY".equals(symbol)) {
                    setUsdJpyRate(new BigDecimal(price).setScale(2, RoundingMode.DOWN));
                } else if ("USDHKD".equals(symbol)) {
                    setUsdHkdRate(new BigDecimal(price).setScale(2, RoundingMode.DOWN));
                } else if ("USDEUR".equals(symbol)) {
                    setUsdEurRate(new BigDecimal(price).setScale(2, RoundingMode.DOWN));
                } else if ("USDGBP".equals(symbol)) {
                    setUsdGbpRate(new BigDecimal(price).setScale(2, RoundingMode.DOWN));
                } else if ("USDCAD".equals(symbol)) {
                    setUsdCadRate(new BigDecimal(price).setScale(2, RoundingMode.DOWN));
                } else if ("USDCHF".equals(symbol)) {
                    setUsdChfRate(new BigDecimal(price).setScale(2, RoundingMode.DOWN));
                } else if ("USDAUD".equals(symbol)) {
                    setUsdAudRate(new BigDecimal(price).setScale(2, RoundingMode.DOWN));
                } else if ("USDAED".equals(symbol)) {
                    setUsdAedRate(new BigDecimal(price).setScale(2, RoundingMode.DOWN));
                }
                body = body.substring(endindex);
                startindex = body.indexOf("datatable-row");
            }
        }
    }

    public Map<String, BigDecimal> getAllRate(String symbol) {
        Map<String, BigDecimal> result = new HashMap<>();
        result.put("USD", getUsdRate(symbol).setScale(2, RoundingMode.DOWN));
        result.put("CNY", getUsdRate(symbol).multiply(usdCnyRate).setScale(2, RoundingMode.DOWN));
        result.put("JPY", getUsdRate(symbol).multiply(usdJpyRate).setScale(2, RoundingMode.DOWN));
        result.put("HKD", getUsdRate(symbol).multiply(usdHkdRate).setScale(2, RoundingMode.DOWN));
        result.put("EUR", getUsdRate(symbol).multiply(usdEurRate).setScale(2, RoundingMode.DOWN));
        result.put("GBP", getUsdRate(symbol).multiply(usdGbpRate).setScale(2, RoundingMode.DOWN));
        result.put("CAD", getUsdRate(symbol).multiply(usdCadRate).setScale(2, RoundingMode.DOWN));
        result.put("CHF", getUsdRate(symbol).multiply(usdChfRate).setScale(2, RoundingMode.DOWN));
        result.put("AUD", getUsdRate(symbol).multiply(usdAudRate).setScale(2, RoundingMode.DOWN));
        result.put("AED", getUsdRate(symbol).multiply(usdAedRate).setScale(2, RoundingMode.DOWN));
        return result;
    }
}
