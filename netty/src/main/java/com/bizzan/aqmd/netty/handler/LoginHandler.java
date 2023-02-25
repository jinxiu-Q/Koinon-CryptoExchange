/*
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: LoginHandler.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月24日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年7月24日, Create
 */
package com.bizzan.aqmd.netty.handler;


import com.bizzan.aqmd.core.annotation.HawkBean;
import com.bizzan.aqmd.core.annotation.HawkMethod;
import com.bizzan.aqmd.core.common.constant.CommonConstant;
import com.bizzan.aqmd.core.common.constant.NettyCommands;
import com.bizzan.aqmd.core.common.constant.NettyResponseCode;
import com.bizzan.aqmd.core.entity.HawkResponseMessage;
import com.bizzan.aqmd.core.entity.LoginMessage;
import com.bizzan.aqmd.core.exception.NettyException;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.channel.ChannelHandlerContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * <p>Title: LoginHandler</p>
 * <p>Description: </p>
 * 登录控制请求，先建立连接，获取sessionId,后发起鉴权，后面所有的请求需要携带sessionId
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @Date 2020年7月24日
 */
@HawkBean
public class LoginHandler{
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	/**
	 * 
	 * <p>Title: login</p>
	 * <p>Description: </p>
	 * 登录成功以后才缓存channel，没有登录成功的连接不会收到推送信息
	 * @param seqId 序列号
	 * @param body 消息体
	 * @param ctx 数据通道
	 * @return 响应内容
	 */
	@HawkMethod(cmd = NettyCommands.LOGIN, version = NettyCommands.COMMANDS_VERSION)
	public HawkResponseMessage.CommonResult login(long seqId, byte[] body, ChannelHandlerContext ctx) {
		Subject subject = SecurityUtils.getSubject();
		LoginMessage.LoginUser user;
		try {
			user = LoginMessage.LoginUser.newBuilder().mergeFrom(body).build();
			UsernamePasswordToken token = new UsernamePasswordToken(
					user.getUsername(), user.getPasswd());
			subject.login(token);//登录

		} catch (InvalidProtocolBufferException e) {
			logger.error(NettyResponseCode.BODY_FORMAT_ERROR.getResponseString());
			throw new NettyException(e, NettyResponseCode.BODY_FORMAT_ERROR.getResponseString());
		}catch(UnknownAccountException|IncorrectCredentialsException e){
			//logger.error(e.getMessage());
			throw new NettyException(e, NettyResponseCode.LOGIN_AUTH_ERROR.getResponseCode()
					+"~"+ NettyResponseCode.LOGIN_AUTH_ERROR.getResponseMessage());
		}
		Session session = subject.getSession();
		session.setAttribute(CommonConstant.LOGINUSER, subject.getPrincipal());
		String userName = Objects.toString(subject.getPrincipal());
		String channelId = ctx.channel().id().asLongText();
		logger.info("[{}]用户登录成功，缓存Channel及Session信息，id分别为：[{}]，[{}]"
				,userName,channelId,session.getId());

		return HawkResponseMessage.CommonResult.newBuilder().setResultCode(NettyResponseCode.SUCCESS.getResponseCode())
				.setResultMsg(NettyResponseCode.SUCCESS.getResponseMessage()).build();
	}

}
