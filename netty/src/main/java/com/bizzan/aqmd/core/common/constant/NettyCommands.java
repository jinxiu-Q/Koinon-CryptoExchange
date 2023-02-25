/*
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: Commands.java</p>
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
 * Title: Commands
 * </p>
 * <p>
 * Description:
 * </p>
 * 客户端和服务器端交互的命令集合
 * <ol>
 * <li>查询：默认为同步请求，命令段号为11,具体命令为三位，如：11001,11002</li>
 * <li>异步查询：当查询逻辑较为复杂时，可以选择异步查询，命令段号为12，具体命令为三位，如：12001,12002,
 * 查询结果保存在消息队列中，有server中的消费者来进行消费，消费完调用channel向客户端返回数据</li>
 * <li>更新：默认为异步请求，命令段号为 22，具体为三位，如：22001,22002</li>
 * <li>同步更新：同步更新结果</li>
 * </ol>
 * 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月19日
 */
public class NettyCommands {
	public static final int COMMANDS_VERSION = 1;
	/**
	 * 连接请求
	 */
	public static final short CONNECT = 11001;
	/**
	 * 登录请求
	 */
	public static final short LOGIN = 11002;
	/**
	 * 登录请求
	 */
	public static final short JSONLOGIN = 11000;
	/**
	 * 建立push通道请求
	 */
	public static final short PUSH_REQUEST = 11003;
	/**
	 * 心跳请求
	 */
	public static final short HEART_BEAT = 11004;

}
