package com.bizzan.bitrade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CoinextDTO {
    private String coinname;
    private Integer protocol;
    private String protocolname;
    private Integer decimals;
    private double withdrawfee;
    private double minwithdrawfee;
    private Integer iswithdraw;
    private Integer isrecharge;
    private Integer isautowithdraw;
    private double minwithdraw;
    private double maxwithdraw;
    private double minrecharge;
    private Integer confirms;
    private String memoaddress;
}
