/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: NettyResponseCode.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月19日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年7月19日, Create
 */
package com.bizzan.aqmd.core.common.constant;

/**
 * <p>
 * Title: NettyResponseCode
 * </p>
 * <p>
 * Description:
 * </p>
 * 响应码配置类
 * 响应码分为两种：
 * <ol>
 * 	<li>请求响应码：暂定为200，201，和500两种，即要么成功，要么失败，成功按正常对象对body进行序列化
 * 		失败或异步操作成功按照commresult进行序列化，获取具体的错误码及错误原因
 * </li>
 * 	<li>业务错误返回码：对应具体的异常码信息</li>
 * <ol>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @Date 2020年7月19日
 */
public class NettyResponseCode {
	
	//======================请求响应吗==============================================
	/**
	 * 正常状态
	 */
	public static final NettyResponseBean SUCCESS = new NettyResponseBean(200, "操作成功");
	/**
	 * 异步请求执行成功
	 */
	public static final NettyResponseBean ASYNC_SUCCESS = new NettyResponseBean(201, "异步请求操作成功");
	/**
	 * 请求执行错误
	 */
	public static final NettyResponseBean REQUEST_ERROR = new NettyResponseBean(502, "请求执行错误");
	
	//=======================业务错误返回码=========================================
	/**
	 * 未知错误
	 */
	public static final NettyResponseBean UNKNOW_ERROR = new NettyResponseBean(500, "未知错误");
	
	/**
	 * protobuffer内容体格式化错误
	 */
	public static final NettyResponseBean BODY_FORMAT_ERROR = new NettyResponseBean(501, "protobuffer内容体格式化错误");
	/**
	 * 指令不存在
	 */
	public static final NettyResponseBean CMD_NOT_FOUND = new NettyResponseBean(404, "指令不存在");
	/**
	 * 过期方法
	 */
	public static final NettyResponseBean OBSOLETED_METHOD = new NettyResponseBean(405, "过期方法");
	/**
	 * UTF-8编码错误
	 */
	public static final NettyResponseBean UTF8_ENCODING_ERROR = new NettyResponseBean(503, "内容无法进行UTF-8转换");
	/**
	 * 未登录
	 */
	public static final NettyResponseBean NOLOGIN_ERROR = new NettyResponseBean(504, "用户会话失效，请重新登录！");
	/**
	 * 登录失败，用户名密码错误
	 */
	public static final NettyResponseBean LOGIN_AUTH_ERROR = new NettyResponseBean(505, "登录失败，用户名或密码错误！");

	/**
	 * 未找到请求对应的处理器
	 */
	public static final NettyResponseBean NO_HANDLER_ERROR = new NettyResponseBean(506, "未找到请求对应的处理器");

	/**
	 * 过滤器IO异常
	 */
	public static final NettyResponseBean FILTER_IO_ERROR = new NettyResponseBean(507, "过滤器IO异常");

	/**
	 * Handler访问权限错误
	 */
	public static final NettyResponseBean HANDLER_ACCESS_ERROR = new NettyResponseBean(508, "Handler访问权限错误");
	/**
	 * Handler访问参数错误
	 */
	public static final NettyResponseBean HANDLER_ARGUMENT_ERROR = new NettyResponseBean(509, "Handler访问参数错误");
	/**
	 * Handler调用异常错误
	 */
	public static final NettyResponseBean HANDLER_INVOCATE_ERROR = new NettyResponseBean(510, "Handler调用异常错误");


}


