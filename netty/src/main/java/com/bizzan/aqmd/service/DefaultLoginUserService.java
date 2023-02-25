/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: LoginUserDao.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年10月26日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年10月26日, Create
 */
package com.bizzan.aqmd.service;

import com.bizzan.aqmd.core.entity.CustomerMsg;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: LoginUserDao</p>
 * <p>Description: </p>
 * netty登陆的默认服务，用户名随意,密码admin
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年10月26日
 */
@SuppressWarnings("rawtypes")
public class DefaultLoginUserService implements LoginUserService {


	@Override
	public CustomerMsg findUserByLoginNo(String loginNo) {
		Map<String,String> result = new HashMap<>();
		CustomerMsg customerMsg = new CustomerMsg();
		customerMsg.setPassword("0c2eea5ef044ce91e0bf4191593c7c1e08126b428c29594de7df5cbdb74b4c90931ee1193b75e50bbc3f8e539605e75a3f2ce88a789d1bfbabf45a1ed2bce849");
		customerMsg.setSalt("123456");
		return customerMsg;
	}


	@Override
	public Integer updPassword(String accountNo, String password) {
		return 1;
	}

}
