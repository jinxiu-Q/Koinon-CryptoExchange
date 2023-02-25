/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: HawkServerRealm.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年10月26日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年10月26日, Create
 */
package com.bizzan.aqmd.netty.shiro.realm;

import com.bizzan.aqmd.core.entity.CustomerMsg;
import com.bizzan.aqmd.netty.shiro.util.PasswordUtil;
import com.bizzan.aqmd.service.LoginUserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.realm.Realm;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>Title: HawkServerRealm</p>
 * <p>Description: </p>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年10月26日
 */
public class HawkServerRealm implements Realm {
	@Autowired
	private LoginUserService loginUserService;
	@Override  
    public String getName() {  
        return "HawkServerRealm";  
    }  
	@Override  
    public boolean supports(AuthenticationToken token) {  
        //仅支持UsernamePasswordToken类型的Token  
        return token instanceof UsernamePasswordToken;   
    }  
    @SuppressWarnings("rawtypes")
	@Override  
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {  
        String loginNo = (String)token.getPrincipal();  //得到用户名  
        String password = new String((char[])token.getCredentials()); //得到密码  
        CustomerMsg dbUser = loginUserService.findUserByLoginNo(loginNo);
	    if(dbUser==null) {
	      throw new UnknownAccountException(); //如果用户名错误  
	    } 
        String dbPwd = dbUser.getPassword();
        String salt =  dbUser.getSalt();
        String digestPwd = PasswordUtil.digestEncodedPassword(password, dbUser.getId()+salt);
        if(!dbPwd.equals(digestPwd)) {
            throw new IncorrectCredentialsException(); //如果密码错误  
        }  
        //如果身份认证验证成功，返回一个AuthenticationInfo实现；  
        return new SimpleAuthenticationInfo(loginNo, password, getName());  
    }
    public static void main(String[] args){
	    System.out.println("2ad18fc87f55c00ba273176a1349633453228dca55a8a9440b9f233a1b26cdd6bff6113206ec2ca2a1541a864e88167e404ff64eee40310c6eef5a420feb9308");
	    System.out.println(PasswordUtil.digestEncodedPassword("d2f7575c5ea7c237725037a267c560f1", "9922286892116869133424271992021244351525401293731"));
    }
}
