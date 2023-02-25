/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: NettyResponseBean.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月20日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年7月20日, Create
 */
package com.bizzan.aqmd.core.common.constant;

/**
 * <p>Title: NettyResponseBean</p>
 * <p>Description: </p>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月20日
 */
public 	class NettyResponseBean {
	private int responseCode;
	private String responseMessage;

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param responseCode
	 * @param responseMessage
	 */
	public NettyResponseBean(int responseCode, String responseMessage) {
		super();
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	/**
	 * 
	 * <p>Title: getResponseString</p>
	 * <p>Description: </p>
	 * 返回错误码和错误信息，用"~"分隔
	 * @return
	 */
	public String getResponseString(){
		return responseCode+"~"+responseMessage;
	}
}