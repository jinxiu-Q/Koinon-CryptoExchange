package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Hevin E-Mali:390330302@qq.com
 * @description
 * @date 2021/12/29 14:14
 */
@Entity
@Data
@Table(name = "convert_coin")
@ToString
public class ConvertCoin {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    /**
     * 排序
     */
    private int sort;
    private int status;

    @Column(columnDefinition = "decimal(18,8) comment '最大兑换数量'")
    private BigDecimal maxAmount;
    @Column(columnDefinition = "decimal(18,8) comment '最小兑换数量'")
    private BigDecimal minAmount;

    private BigDecimal fee;

    @NotBlank(message = "币种不能为空")
    private String coinUnit;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
