package com.bizzan.bitrade.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Document(collection = "exchange_order_detail")
public class ExchangeOrderDetail implements Serializable {
    @Indexed
    private String orderId;
    private BigDecimal price;
    private BigDecimal amount;
    private BigDecimal turnover;
    private BigDecimal fee;
    //成交时间
    private long time;
}
