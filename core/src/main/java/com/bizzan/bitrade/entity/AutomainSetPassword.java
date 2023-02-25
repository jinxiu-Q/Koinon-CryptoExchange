package com.bizzan.bitrade.entity;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
public class AutomainSetPassword {

    @NotNull(message = "协议ID不得为空")
    private Integer protocol;

    @NotNull(message = "密码不得为空")
    private String password;
}
