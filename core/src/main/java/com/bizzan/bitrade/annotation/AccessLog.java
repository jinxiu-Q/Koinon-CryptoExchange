package com.bizzan.bitrade.annotation;


import com.bizzan.bitrade.constant.AdminModule;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLog {
    String operation();
    AdminModule module();
}

