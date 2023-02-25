package com.bizzan.bitrade.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Hevin E-Mali:390330302@qq.com
 * @Title: ${file_name}
 * @Description:
 * @date 2021/4/289:45
 */
@Data
public class ContractDTO {
    private String name;
    private String address;
    private int decimals;
    private BigDecimal minCollectAmount;
}
