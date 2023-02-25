package com.bizzan.bitrade.model.screen;

import lombok.Data;

@Data
public class WithdrawScreen {

    private String email;

    private String tel;

    private String address;

    private Integer protocol;

    private String coinname;

    private Integer status;

    private String hash;

    private String startAddTime;

    private String endAddTime;

    private String startProcessTime;

    private String endProcessTime;

    // 是否导出（0：否，1：是）
    private Integer isOut;
}
