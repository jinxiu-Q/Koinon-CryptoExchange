package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.DepositStatusEnum;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 缴纳押金记录（包括缴纳押金和取回押金）
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2019/5/5
 */
@Entity
@Data
@Table(name = "deposit_record")
public class DepositRecord {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "coin_id")
    private Coin coin;

    private BigDecimal amount;

    /**
     * 动作，缴纳或索回
     */
    @Enumerated(EnumType.ORDINAL)
    private DepositStatusEnum status;
}
