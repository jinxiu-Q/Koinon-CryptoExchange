package com.bizzan.bitrade.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MessageEncrypt {

    private String message;

    private String password;
}
