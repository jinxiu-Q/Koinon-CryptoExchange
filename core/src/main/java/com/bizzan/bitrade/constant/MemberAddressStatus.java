package com.bizzan.bitrade.constant;

import com.bizzan.bitrade.core.BaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
public enum MemberAddressStatus implements BaseEnum {
    /**
     * 表示正常状态
     */
    NORMAL("正常"),
    /**
     * 表示删除了
     */
    ILLEGAL("删除"),
    /**
     * 表示还没有在邮件中确认
     */
    WAITING("未确认");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal(){
        return this.ordinal();
    }
}