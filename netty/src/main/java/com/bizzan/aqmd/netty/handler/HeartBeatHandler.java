/*
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: HeartBeatHandler.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年8月8日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年8月8日, Create
 */
package com.bizzan.aqmd.netty.handler;


import com.bizzan.aqmd.core.annotation.HawkBean;
import com.bizzan.aqmd.core.annotation.HawkMethod;
import com.bizzan.aqmd.core.common.constant.NettyCommands;
import com.bizzan.aqmd.core.common.constant.NettyResponseCode;
import com.bizzan.aqmd.core.entity.HawkResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: HeartBeatHandler</p>
 * <p>Description: </p>
 * 心跳包处理请求由于移动无线网络的特点，推送服务的心跳周期并不能设置的太长，
 * 否则长连接会被释放，造成频繁的客户端重连，但是也不能设置太短，
 * 否则在当前缺乏统一心跳框架的机制下很容易导致信令风暴（例如微信心跳信令风暴问题）。
 * 具体的心跳周期并没有统一的标准，180S也许是个不错的选择，微信为300S
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @Date 2020年8月8日
 */
@HawkBean
public class HeartBeatHandler {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	/**
	 *
	 * @param seqId 请求id
	 * @param body 请求体
	 * @param ctx 通道
	 * @return 响应
	 */
	@HawkMethod(cmd = NettyCommands.HEART_BEAT, version = NettyCommands.COMMANDS_VERSION)
	public HawkResponseMessage.CommonResult heartBeat(long seqId, byte[] body, ChannelHandlerContext ctx) {
		return HawkResponseMessage.CommonResult.newBuilder().setResultCode(NettyResponseCode.SUCCESS.getResponseCode())
				.setResultMsg("").build();
	}
}
