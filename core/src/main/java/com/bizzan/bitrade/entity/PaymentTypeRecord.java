package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.vo.PaymentTypeConfig;
import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

/**
 * 用户支付方式绑定记录表
 */
@Data
@Entity
public class PaymentTypeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 用户id
     */
    private Long memberId;
    /**
     * 支付方式id
     */
    private Long type;

    private String field_1;

    private String field_2;

    private String field_3;

    private String field_4;

    private String field_5;

    private String field_6;

    private String field_7;

    @Transient
    private String typeName;
    @Transient
    private Map<String,String> fieldType;
    @Transient
    private String color;
}
