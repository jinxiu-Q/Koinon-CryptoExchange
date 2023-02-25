package com.bizzan.bitrade.model.vo;

import com.bizzan.bitrade.annotation.Excel;
import lombok.Data;

@Data
public class OtcOrderExcelVO {
    @Excel(name = "订单编号")
    private String orderSn;
    @Excel(name = "交易时间")
    private String createTime;
    @Excel(name = "交易人")
    private String customerName;
    @Excel(name = "创建人")
    private String memberName;
    @Excel(name = "币种")
    private String unit;
    @Excel(name = "类型")
    private String advertiseType;
    @Excel(name = "订单数量")
    private String number;
    @Excel(name = "订单金额")
    private String money;
    @Excel(name = "手续费")
    private String fee;
    @Excel(name = "支付方式")
    private String payMode;
    @Excel(name = "订单状态")
    private String status;

}
