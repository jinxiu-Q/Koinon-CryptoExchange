package com.bizzan.bitrade.entity;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 盘口信息
 */
@Data
@Slf4j
public class TradePlate {
    private List<TradePlateItem> items;
    //最大深度
    private int maxDepth = 100;
    //方向
    private ExchangeOrderDirection direction;
    private String symbol;
    public TradePlate(){

    }

    public TradePlate(String symbol, ExchangeOrderDirection direction) {
        this.direction = direction;
        this.symbol = symbol;
        items = Collections.synchronizedList(new LinkedList<TradePlateItem>());
    }

    public boolean add(ExchangeOrder exchangeOrder) {
        //log.info("add TradePlate order={}",exchangeOrder);
        synchronized (items) {
            int index = 0;
            if (exchangeOrder.getType() == ExchangeOrderType.MARKET_PRICE) {
                return false;
            }
            if (exchangeOrder.getDirection() != direction) {
                return false;
            }
            if (items.size() > 0) {
                for (index = 0; index < items.size(); index++) {
                    TradePlateItem item = items.get(index);
                    if (exchangeOrder.getDirection() == ExchangeOrderDirection.BUY && item.getPrice().compareTo(exchangeOrder.getPrice()) > 0
                            || exchangeOrder.getDirection() == ExchangeOrderDirection.SELL && item.getPrice().compareTo(exchangeOrder.getPrice()) < 0) {
                        continue;
                    } else if (item.getPrice().compareTo(exchangeOrder.getPrice()) == 0) {
                        BigDecimal deltaAmount = exchangeOrder.getAmount().subtract(exchangeOrder.getTradedAmount());
                        item.setAmount(item.getAmount().add(deltaAmount));
                        return true;
                    } else {
                        break;
                    }
                }
            }
            if(index < maxDepth) {
                TradePlateItem newItem = new TradePlateItem();
                newItem.setAmount(exchangeOrder.getAmount().subtract(exchangeOrder.getTradedAmount()));
                newItem.setPrice(exchangeOrder.getPrice());
                items.add(index, newItem);
            }
        }
        return true;
    }

    public void remove(ExchangeOrder order,BigDecimal amount) {
        synchronized (items) {
            //log.info("items>>init_size={},orderPrice={}",items.size(),order.getPrice());
            for (int index = 0; index < items.size(); index++) {
                TradePlateItem item = items.get(index);
                if (item.getPrice().compareTo(order.getPrice()) == 0) {
                    item.setAmount(item.getAmount().subtract(amount));
                    if (item.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                        items.remove(index);
                    }
                    //log.info("items>>final_size={},itemAmount={},itemPrice={}",items.size(),item.getAmount(),item.getPrice());
                    return;
                }
            }
            log.info("items>>return_size={}",items.size());
        }
    }

    public void remove(ExchangeOrder order){
        remove(order,order.getAmount().subtract(order.getTradedAmount()));
    }

    public void setItems(LinkedList<TradePlateItem> items){
        this.items = items;
    }

    public BigDecimal getHighestPrice(){
//        synchronized (items) {
        if (items == null || items.size() == 0) {
            return BigDecimal.ZERO;
        }
        if (direction == ExchangeOrderDirection.BUY) {
            return items.get(0).getPrice();
        } else {
            return items.get(items.size()-1).getPrice();
        }
//        }
    }

    public int getDepth(){
        return items==null?0:items.size();
    }


    public BigDecimal getLowestPrice(){
//        synchronized (items) {
        if (items == null || items.size() == 0) {
            return BigDecimal.ZERO;
        }
        if (direction == ExchangeOrderDirection.BUY) {
            return items.get(items.size()-1).getPrice();
        } else {
            return items.get(0).getPrice();
        }
//        }
    }

    /**
     * 获取委托量最大的档位
     * @return
     */
    public BigDecimal getMaxAmount(){
//        synchronized (items) {
        if (items == null || items.size() == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal amount = BigDecimal.ZERO;
        for (TradePlateItem item : items) {
            if (item.getAmount().compareTo(amount) > 0) {
                amount = item.getAmount();
            }
        }
        return amount;
//        }

    }

    /**
     * 获取委托量最小的档位
     * @return
     */
    public BigDecimal getMinAmount(){
//        synchronized (items) {
        if (items == null || items.size() == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal amount = items.get(0).getAmount();
        for (TradePlateItem item : items) {
            if (item.getAmount().compareTo(amount) < 0) {
                amount = item.getAmount();
            }
        }
        return amount;
//        }
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("direction", direction);
        json.put("maxAmount", getMaxAmount());
        json.put("minAmount", getMinAmount());
        json.put("highestPrice", getHighestPrice());
        json.put("lowestPrice", getLowestPrice());
        json.put("symbol", getSymbol());
        json.put("items", items);
        return json;
    }

    public JSONObject toJSON(int limit){
        JSONObject json = new JSONObject();
        json.put("direction", direction);
        json.put("maxAmount", getMaxAmount());
        json.put("minAmount", getMinAmount());
        json.put("highestPrice", getHighestPrice());
        json.put("lowestPrice", getLowestPrice());
        json.put("symbol", getSymbol());
        json.put("items", items.size() > limit ? items.subList(0, limit) : items);
        return json;
    }


    public String toJSONString(){
        synchronized (items){
            return JSON.toJSONString(this);
        }
    }
}
