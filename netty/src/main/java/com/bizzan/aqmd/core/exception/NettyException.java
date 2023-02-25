/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: NettyException.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月18日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年7月18日, Create
 */
package com.bizzan.aqmd.core.exception;


import com.bizzan.aqmd.core.common.constant.NettyResponseBean;

/**
 * <p>Title: NettyException</p>
 * <p>Description: </p>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月18日
 */
public class NettyException extends BaseException {
	/** 
     * Constructors 
     *
     * @param code
     *            错误代码
     */  
    public NettyException(String code) {
        super(code, null, code, null);  
    }  

    public NettyException(NettyResponseBean responseBean){
        super(responseBean.getResponseString(),null,
                responseBean.getResponseString(),null  );
    }
    /** 
     * Constructors 
     *  
     * @param cause 
     *            异常接口 
     * @param code 
     *            错误代码 
     */  
    public NettyException(Throwable cause, String code) {
        super(code, cause, code, null);  
    }  
  
    /** 
     * Constructors 
     *  
     * @param code 
     *            错误代码 
     * @param values 
     *            一组异常信息待定参数 
     */  
    public NettyException(String code, Object[] values) {
        super(code, null, code, values);  
    }  
  
    /** 
     * Constructors 
     *  
     * @param cause 
     *            异常接口 
     * @param code 
     *            错误代码 
     * @param values 
     *            一组异常信息待定参数 
     */  
    public NettyException(Throwable cause, String code, Object[] values) {
        super(code, null, code, values);  
    }  
  
    private static final long serialVersionUID = -3711290613973933714L;  
}
