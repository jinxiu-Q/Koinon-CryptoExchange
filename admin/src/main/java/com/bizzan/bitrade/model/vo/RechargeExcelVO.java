package com.bizzan.bitrade.model.vo;

import com.bizzan.bitrade.annotation.Excel;
import lombok.Data;

@Data
public class RechargeExcelVO {
    @Excel(name = "用户ID")
    private Long memberId;
    @Excel(name = "邮箱")
    private String email;
    @Excel(name = "手机号")
    private String mobilePhone;
    @Excel(name = "充值币种")
    private String coinname;

    @Excel(name = "协议名称")
    private String protocolname;

    @Excel(name = "充币地址")
    private String address;

    @Excel(name = "充值数量")
    private String money;

    @Excel(name = "状态")
    private String status;

    @Excel(name = "确认数")
    private String confirms;

    @Excel(name = "到账时间")
    private String addtime;

}
