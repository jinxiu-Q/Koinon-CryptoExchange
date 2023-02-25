package com.bizzan.bitrade.model.screen;

import lombok.Data;

@Data
public class RechargeScreen {

    private String address;

    private Integer protocol;

    private String coinname;

    // 是否导出（0：否，1：是）
    private Integer isOut;
}
