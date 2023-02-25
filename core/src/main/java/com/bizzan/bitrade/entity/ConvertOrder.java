package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Hevin E-Mali:390330302@qq.com
 * @description
 * @date 2021/12/29 14:14
 */
@Entity
@Data
@Table(name = "convert_order")
@ToString
public class ConvertOrder {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @NotNull
    private Long memberId; // 兑换人

    private String fromUnit; // 源币种

    private String toUnit; // 目标币种

    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal price; // 价格

    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal fromAmount; // 源兑换数量

    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal fee; // 手续费

    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal toAmount; // 目标兑换数量

    // 状态（0：未成交， 1：已成交， 2：用户取消， 3：管理员撤回）
    private int status = 0;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
