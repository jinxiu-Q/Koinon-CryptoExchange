package com.bizzan.bitrade.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 承兑商名单列表
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 *
 */
@Entity
@Data
@Table
public class CtcAcceptor {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
	
	@ManyToOne
    @JoinColumn(name = "member_id")
	private Member member;
	
	@Column(columnDefinition = "decimal(18,2) default 0.00 comment '售出USDT'")
	private BigDecimal usdtOut = BigDecimal.ZERO;
	
	@Column(columnDefinition = "decimal(18,2) default 0.00 comment '买入USDT'")
	private BigDecimal usdtIn = BigDecimal.ZERO;
	
	@Column(columnDefinition = "decimal(18,2) default 0.00 comment '售出CNY'")
	private BigDecimal cnyOut = BigDecimal.ZERO;
	
	@Column(columnDefinition = "decimal(18,2) default 0.00 comment '买入CNY'")
	private BigDecimal cnyIn = BigDecimal.ZERO;
	
	/**
	 * 状态（0：无效，1：有效）
	 */
	private int status = 1;
}
