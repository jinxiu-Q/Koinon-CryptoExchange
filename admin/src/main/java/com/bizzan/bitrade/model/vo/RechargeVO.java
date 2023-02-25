package com.bizzan.bitrade.model.vo;

import lombok.Data;

@Data
public class RechargeVO {

    private Integer id;

    private String hash;

    private String md5;

    private Integer memberid;

    private Long addtime;

    private Long coinid;

    private String coinname;

    private double money;

    private Integer block;

    private Integer confirms;

    private Integer nconfirms;

    // 0 未到账 1 已到账 -1 失败
    private Integer status;

    private String send;

    private String address;

    private Integer protocol;

    private String protocolname;

    private String username;

}
