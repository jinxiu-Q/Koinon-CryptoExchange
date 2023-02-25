package com.bizzan.bitrade.model.vo;

import com.bizzan.bitrade.annotation.Excel;
import lombok.Data;

@Data
public class WithdrawExcelVO {
    @Excel(name = "用户ID")
    private Long memberId;
    @Excel(name = "邮箱")
    private String email;
    @Excel(name = "手机号")
    private String mobilePhone;
    @Excel(name = "提现币种")
    private String coinname;

    @Excel(name = "协议名称")
    private String protocolname;

    @Excel(name = "到账地址")
    private String address;

    @Excel(name = "提现数量")
    private String money;

    @Excel(name = "手续费")
    private String fee;

    @Excel(name = "到账数量")
    private String real_money;

    @Excel(name = "提现时间")
    private String addtime;

    @Excel(name = "审核时间")
    private String processtime;

    @Excel(name = "Hash")
    private String hash;

    @Excel(name = "状态")
    private String status;

}
