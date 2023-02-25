package com.bizzan.bitrade.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: Field
 * @author: Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @create: 2021/07/04 14:48
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Field {
    String value();
}
