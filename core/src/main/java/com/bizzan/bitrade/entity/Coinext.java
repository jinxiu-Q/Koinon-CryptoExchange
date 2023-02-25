package com.bizzan.bitrade.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "coinext")
@ToString
public class Coinext {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Excel(name = "ID", orderNum = "1", width = 20)
    private Integer id;

    @Excel(name = "币种ID", orderNum = "1", width = 20)
    private Integer coinid;

    @Excel(name = "币种名称", orderNum = "1", width = 20)
    @NotBlank(message = "币种名称不得为空")
    private String coinname;

    @Excel(name = "协议ID", orderNum = "1", width = 20)
    @NotNull(message = "协议ID不得为空")
    private Integer protocol;

    @Excel(name = "协议名称", orderNum = "1", width = 20)
    private String protocolname;

    @Excel(name = "提现私钥", orderNum = "1", width = 20)
    private String mainaddress;

    @Excel(name = "合约地址", orderNum = "1", width = 20)
    private String ext;

    @Excel(name = "币种精度", orderNum = "1", width = 20)
    private Integer decimals;

    // 0 关闭 1 启动
    @Excel(name = "状态", orderNum = "1", width = 20)
    private Integer status;

    @Excel(name = "提现手续费", orderNum = "1", width = 20)
    private double withdrawfee;

    @Excel(name = "最低提现手续费数量", orderNum = "1", width = 20)
    private double minwithdrawfee;

    // 0：否，1：是
    @Excel(name = "是否开启提现", orderNum = "1", width = 20)
    private Integer iswithdraw;

    // 0：否，1：是
    @Excel(name = "是否开启充值", orderNum = "1", width = 20)
    private Integer isrecharge;

    // 0：否，1：是
    @Excel(name = "是否开启自动提现（不建议开启）", orderNum = "1", width = 20)
    private Integer isautowithdraw;

    @Excel(name = "最低提现数量", orderNum = "1", width = 20)
    private double minwithdraw;

    @Excel(name = "最大提现数量", orderNum = "1", width = 20)
    private double maxwithdraw;

    @Excel(name = "最低充值", orderNum = "1", width = 20)
    private double minrecharge;

    @Excel(name = "确认到账次数", orderNum = "1", width = 20)
    private Integer confirms;

    @Excel(name = "用户填写备注码充值的地址 如果不需要填空", orderNum = "1", width = 20)
    private String memoaddress;
}
