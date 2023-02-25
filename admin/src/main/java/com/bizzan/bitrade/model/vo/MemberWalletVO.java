package com.bizzan.bitrade.model.vo;

import com.bizzan.bitrade.annotation.Excel;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class MemberWalletVO {

    private Long id ;

    @Excel(name="用户ID")
    private Long memberId = 0L;

    @Excel(name="用户名")
    private String username = "";

    @Excel(name="邮箱")
    private String email = "";

    @Excel(name="手机号")
    private String mobilePhone = "";

    @Excel(name="真实姓名")
    private String realName = "";

    @Excel(name="币种名称")
    private String unit = "";

    @Excel(name="钱包地址")
    private String address = "";

    @Excel(name="可用币数")
    private String balance = "";

    @Excel(name="冻结币数")
    private String frozenBalance = "";

    @Excel(name="总币个数")
    private String allBalance = "";
}
