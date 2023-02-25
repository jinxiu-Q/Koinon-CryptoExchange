/*
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: HawkBean.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月18日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年7月18日, Create
 */
package com.bizzan.aqmd.core.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * <p>Title: HawkBean</p>
 * <p>Description: </p>
 * 在服务类中标该类，以便确定服务方法所属的组及相关信息
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月18日
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface HawkBean {
    /**
     * 指令版本号
     * @return
     */
    byte version() default 0;
}
