/*
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: ServerStatus.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年6月26日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年6月26日, Create
 */
package com.bizzan.aqmd.netty.common;

/**
 * <p>Title: ServerStatus</p>
 * <p>Description: </p>
 * 服务器状态的情况罗列，分为：<br/>
 * 默认：DEFAULT(0)；
 * 初始化：INIT(1)；
 * 可用状态：ALIVE(2);
 * 不可用状态：DEAD(3);
 * 关闭状态：CLOSE(4)
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年6月26日
 */
public enum ServerStatus {
	DEFAULT(0),
	INIT(1),
	ALIVE(2),
	DEAD(3),
	CLOSE(4);
	public final int value;
	private ServerStatus(int value){
		this.value = value;
	}
	/**
	 * 
	 * <p>Title: isClose</p>
	 * <p>Description: </p>
	 * 判断当前值是否是关闭
	 * @return 是否关闭
	 */
	public boolean isClose(){
		return this == CLOSE;
	}
	/**
	 * 
	 * <p>Title: isAlive</p>
	 * <p>Description: </p>
	 * 判断当前值是否是活跃状态
	 * @return 是否活跃
	 */
	public boolean isAlive(){
		return this == ALIVE;
	}
}
