/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: PassworldUtil.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年10月26日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年10月26日, Create
 */
package com.bizzan.aqmd.netty.shiro.util;

import org.apache.shiro.crypto.hash.ConfigurableHashService;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashRequest;

/**
 * <p>Title: PassworldUtil</p>
 * <p>Description: </p>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年10月26日
 */
public class PasswordUtil {
	//默认加密算法
    public static final String DEFAULT_ALGORITHM="SHA-512";
    /**
     * 加密具体方法，使用shiro提供的加密方法
     * @param passworld
     * @param salt
     * @return
     */
    public static String digestEncodedPassword(final String passworld,String salt){
        final ConfigurableHashService hashService = new DefaultHashService();
        hashService.setHashAlgorithmName(DEFAULT_ALGORITHM);
        hashService.setHashIterations(0);
        final HashRequest request = new HashRequest.Builder()
                .setSalt(salt)
                .setSource(passworld)
                .build();
        return hashService.computeHash(request).toHex();
    }
}
