package com.bizzan.bitrade.model.vo;

import com.bizzan.bitrade.annotation.Excel;
import lombok.Data;

@Data
public class ContractOrderEntrustOutVO {
    @Excel(name = "用户ID")
    private Long memberId;
    @Excel(name = "邮箱")
    private String email;
    @Excel(name = "手机号")
    private String mobilePhone;
    @Excel(name = "真实姓名")
    private String realName;
    @Excel(name = "合约")
    private String symbol;
    @Excel(name = "类型")
    private String direction; // 转换
    @Excel(name = "委托类型")
    private String type; // 转换
    @Excel(name = "委托张数")
    private String volume; // 转换
    @Excel(name = "触发价")
    private String triggerPrice; // 转换
    @Excel(name = "委托价")
    private String entrustPrice; // 转换
    @Excel(name = "成交价")
    private String tradedPrice; // 转换
    @Excel(name = "成交张数")
    private String tradedVolume; // 转换
    @Excel(name = "开仓手续费")
    private String openFee; // 转换
    @Excel(name = "平仓手续费")
    private String closeFee; // 转换
    @Excel(name = "平仓盈亏")
    private String profitAndLoss; // 转换
    @Excel(name = "状态")
    private String status; // 转换
    @Excel(name = "挂单时间")
    private String createTime; // 转换

}
