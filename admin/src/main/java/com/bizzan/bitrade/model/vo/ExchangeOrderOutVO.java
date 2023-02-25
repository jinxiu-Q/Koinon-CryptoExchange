package com.bizzan.bitrade.model.vo;

import com.bizzan.bitrade.annotation.Excel;
import lombok.Data;

@Data
public class ExchangeOrderOutVO {

    @Excel(name = "订单号")
    private String orderId;
    @Excel(name = "用户ID")
    private Long memberId;
    @Excel(name = "邮箱")
    private String email;
    @Excel(name = "手机号")
    private String mobilePhone;
    @Excel(name = "真实姓名")
    private String realName;
    @Excel(name = "交易对")
    private String symbol;
    @Excel(name = "委托量")
    private String amount = "--"; // 转换
    @Excel(name = "成交量")
    private String tradedAmount = "--"; // 转换
    @Excel(name = "挂单类型")
    private String type = "--"; // 转换
    @Excel(name = "订单方向")
    private String direction = "--"; // 转换
    @Excel(name = "挂单价格")
    private String price = "--"; // 转换
    @Excel(name = "挂单时间")
    private String time = "--"; // 转换
    @Excel(name = "状态")
    private String status = "--"; // 转换
}
