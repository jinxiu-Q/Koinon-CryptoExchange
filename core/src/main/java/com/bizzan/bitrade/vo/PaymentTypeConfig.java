package com.bizzan.bitrade.vo;

import lombok.Data;

@Data
public class PaymentTypeConfig {
    private String fieldName;//字段名称
    private Boolean require;//是否必填
    private String showText;//显示名称
    private String placeholder;//输入框内显示
    private String type;//类型 input输入框  image图片  tip提示
}
