package com.bizzan.bitrade.model.vo;

import lombok.Data;

@Data
public class WithdrawVO {

    private Integer id;
    private Integer memberid;
    private Long addtime;
    private Integer coinid;
    private String coinname;
    private String address;
    private double money;
    private double fee;
    private double real_money;
    private Integer processmold;
    private String hash;
    private Integer status;
    private Long processtime;
    private String withdrawinfo;
    private String remark;
    private Integer protocol;
    private String protocolname;

    private String username;
    private String email;

}
