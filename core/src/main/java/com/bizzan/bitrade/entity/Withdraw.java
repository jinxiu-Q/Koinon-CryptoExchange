package com.bizzan.bitrade.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Data
@Table(name = "withdraw")
@ToString
@DynamicUpdate
public class Withdraw {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Excel(name = "ID", orderNum = "1", width = 20)
    private Integer id;

    @Excel(name = "用户ID", orderNum = "1", width = 20)
    private Integer memberid;

    @Excel(name = "添加时间", orderNum = "1", width = 20)
    private Long addtime;

    @Excel(name = "币种ID", orderNum = "1", width = 20)
    private Integer coinid;

    @Excel(name = "币种名称", orderNum = "1", width = 20)
    private String coinname;

    @Excel(name = "提现地址", orderNum = "1", width = 20)
    private String address;

    @Excel(name = "申请金额", orderNum = "1", width = 20)
    private double money;

    @Excel(name = "提现手续费", orderNum = "1", width = 20)
    private double fee;

    @Excel(name = "真实到账数量", orderNum = "1", width = 20)
    private double real_money;

    // *处理模式，0区块处理，1外部处理
    @Excel(name = "处理模式", orderNum = "1", width = 20)
    private Integer processmold;

    @Excel(name = "哈希", orderNum = "1", width = 20)
    private String hash;

    // *状态，-1,驳回,0待处理,1处理中,2已处理
    @Excel(name = "状态", orderNum = "1", width = 20)
    private Integer status;

    @Excel(name = "后台处理时间", orderNum = "1", width = 20)
    private Long processtime;

    @Excel(name = "提现失败原因", orderNum = "1", width = 20)
    private String withdrawinfo;

    @Excel(name = "用户申请备注", orderNum = "1", width = 20)
    private String remark;

    @Excel(name = "协议ID", orderNum = "1", width = 20)
    private Integer protocol;

    @Excel(name = "协议名称", orderNum = "1", width = 20)
    private String protocolname;
}
