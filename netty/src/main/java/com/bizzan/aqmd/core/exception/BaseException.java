/*
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: BaseException.java</p>
 * 
 * Description: 异常基类
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年11月18日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年11月18日, Create
 */
package com.bizzan.aqmd.core.exception;

/**
 * <p>Title: BaseException</p>
 * <p>Description: </p>
 * 异常基类，各个模块的运行期异常均继承与该类 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年11月18日
 */
public class BaseException extends RuntimeException {
	/** 
     * the serialVersionUID 
     */  
    private static final long serialVersionUID = 1381325479896057076L;  
  
    /** 
     * message key 
     */  
    private String code;  
  
    /** 
     * message params 
     */  
    private Object[] values;  
  
    /** 
     * @return the code 
     */  
    public String getCode() {  
        return code;  
    }  
  
    /** 
     * @param code the code to set 
     */  
    public void setCode(String code) {  
        this.code = code;  
    }  
  
    /** 
     * @return the values 
     */  
    public Object[] getValues() {  
        return values;  
    }  
  
    /** 
     * @param values the values to set 
     */  
    public void setValues(Object[] values) {  
        this.values = values;  
    }  
  
    public BaseException(String message, Throwable cause, String code, Object[] values) {  
        super(message, cause);  
        this.code = code;  
        this.values = values;  
    } 
    public BaseException(Throwable cause) {  
        super(cause);  
    }
}
