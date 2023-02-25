package com.bizzan.bitrade.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;


@Data
@Entity
public class PaymentType {
    /**
     * 中文名称
     */
    @Id
    private Long id;
    /**
     * 支付方式简码
     */
    private String code;

    /**
     * 支付方式配置json
     */
    private String configJson;
    

}
