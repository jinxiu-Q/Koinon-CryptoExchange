package com.bizzan.bitrade.processor;

import com.bizzan.bitrade.entity.ExchangeOrderCricle;

import java.math.BigDecimal;

public interface ContractProcessor {

    public void setCurPrice(BigDecimal price);

    public BigDecimal getCurPrice();

    public void addExchangeOrderCircle(ExchangeOrderCricle order);

    public void cancelExchangeOrderCircle(ExchangeOrderCricle order);

    public void handle(long time);
}
