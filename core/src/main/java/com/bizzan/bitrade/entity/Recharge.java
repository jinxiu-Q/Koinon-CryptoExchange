package com.bizzan.bitrade.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@Table(name = "recharge")
@ToString
public class Recharge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Excel(name = "ID", orderNum = "1", width = 20)
    private Integer id;

    @Excel(name = "交易哈希", orderNum = "1", width = 20)
    private String hash;

    @Excel(name = "哈希MD5", orderNum = "1", width = 20)
    private String md5;

    @Excel(name = "用户ID", orderNum = "1", width = 20)
    private Integer memberid;

    @Excel(name = "添加时间", orderNum = "1", width = 20)
    private Long addtime;

    @Excel(name = "币种ID", orderNum = "1", width = 20)
    private Long coinid;

    @Excel(name = "币种名称", orderNum = "1", width = 20)
    private String coinname;

    @Excel(name = "金额", orderNum = "1", width = 20)
    private double money;

    @Excel(name = "区块", orderNum = "1", width = 20)
    private Long block;

    @Excel(name = "已确认次数", orderNum = "1", width = 20)
    private Integer confirms;

    @Excel(name = "需要确认的次数", orderNum = "1", width = 20)
    private Integer nconfirms;

    // 0 未到账 1 已到账 -1 失败
    @Excel(name = "状态", orderNum = "1", width = 20)
    private Integer status;

    @Excel(name = "发送方地址", orderNum = "1", width = 20)
    private String send;

    @Excel(name = "接收方地址", orderNum = "1", width = 20)
    private String address;

    @Excel(name = "协议ID", orderNum = "1", width = 20)
    private Integer protocol;

    @Excel(name = "协议名称", orderNum = "1", width = 20)
    private String protocolname;

    //0 为正常区块记录 1 后台增加
    private Integer agreen;

}
