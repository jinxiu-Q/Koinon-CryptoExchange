package com.bizzan.bitrade.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年12月19日
 */
@AllArgsConstructor
@Getter
public enum AdminModule {
    CMS("CMS"),
    COMMON("COMMON"),
    EXCHANGE("EXCHANGE"),
    FINANCE("FINANCE"),
    MEMBER("MEMBER"),
    OTC("OTC"),
    SYSTEM("SYSTEM"),
    PROMOTION("PROMOTION"),
    INDEX("INDEX"),
	ACTIVITY("ACTIVITY"),
	CTC("CTC"),
	REDENVELOPE("REDENVELOPE"),
    CONTRACTOPTION("CONTRACTOPTION");
	
    @Setter
    private String title;
}