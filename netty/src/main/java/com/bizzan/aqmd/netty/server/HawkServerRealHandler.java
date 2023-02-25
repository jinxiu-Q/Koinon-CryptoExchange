/*
 * Copyright (c) 2016-2017  All Rights Reserved.
 *
 * <p>FileName: HawkServerRealHandler.java</p>
 *
 * Description:
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年6月26日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年6月26日, Create
 */
package com.bizzan.aqmd.netty.server;

import com.bizzan.aqmd.netty.common.NettyCacheUtils;
import com.bizzan.aqmd.service.ChannelEventDealService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultChannelPromise;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * <p>
 * Title: HawkServerRealHandler
 * </p>
 * <p>
 * Description:
 * </p>
 *
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @Date 2020年6月26日
 */
@Sharable
public class HawkServerRealHandler extends HawkServerHandler {
	private static final Logger logger = LoggerFactory.getLogger(HawkServerRealHandler.class);
	@Autowired
	private ChannelEventDealService channelEventDealService;
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		super.userEventTriggered(ctx, evt);
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (IdleState.READER_IDLE == event.state()) { // 读空闲
				logger.info("id:0x{}，读空闲", ctx.channel().id().asLongText());
			} else if (IdleState.WRITER_IDLE == event.state()) { // 写空闲
				logger.info("id:0x{}，写空闲", ctx.channel().id().asLongText());
			} else if (IdleState.ALL_IDLE == event.state()) { // 读写空闲
				logger.info("id:0x{}，读写空闲", ctx.channel().id().asLongText());
				ctx.close(
						new DefaultChannelPromise(ctx.channel()).addListener((ChannelFutureListener) channelFuture -> {
							logger.error("timeoutClose");
							logger.info("channel[0x{}] timeout closed", ctx.channel().id().asLongText());
						}));
			}
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		try {
			InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
			InetSocketAddress localAddress = (InetSocketAddress)ctx.channel().localAddress();
			logger.info("channel[{}] from {}:{} actived.", ctx.channel().id().asLongText(), remoteAddress.getAddress().getHostAddress(), remoteAddress.getPort());
			channelEventDealService.dealChannelActive(localAddress.getAddress().getHostAddress(),remoteAddress.getAddress().getHostAddress(), remoteAddress.getPort());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 断开连接的时候
	 */
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx){
		try {
			InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
			InetSocketAddress localAddress = (InetSocketAddress)ctx.channel().localAddress();
			Channel channel = ctx.channel();
			logger.info("channel[{}] from {}:{} disconnected.", ctx.channel().id().asLongText(), remoteAddress.getAddress().getHostAddress(), remoteAddress.getPort());
			String user =  NettyCacheUtils.keyChannelCache.get(channel);
			if(user!=null){//存在推送
				logger.info("remove the push request from memeory");
				Set<Channel> channels = NettyCacheUtils.getChannel(user);
				if(!CollectionUtils.isEmpty(channels)){//存在直接通过push_request请求建立的推送
					boolean flag = channels.remove(channel);
					logger.info("user[{}] channel remove :"+flag,user);
				}
				Set<String> keys  = NettyCacheUtils.userKey.get(user);
				if(!CollectionUtils.isEmpty(keys)){
					logger.info("need remove keys,total[{}]",keys.size());
					keys.forEach(key->{
						Set<Channel> keyChannels = NettyCacheUtils.getChannel(key);
						if(!CollectionUtils.isEmpty(keyChannels)){//存在直接通过push_request请求建立的推送
							boolean flag = keyChannels.remove(channel);
							logger.debug("key[{}] channel remove :"+flag,key);
						}
					});
				}

			}
			channelEventDealService.dealChannelDestory(localAddress.getAddress().getHostAddress(),remoteAddress.getAddress().getHostAddress(), remoteAddress.getPort());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 给客户端发消息
	 *
	 * @param username 用户名
	 * @param cmd 指令名称
	 * @return 终端在线返回true，否则返回false
	 */
	public static int writeAndFlush(String username, short cmd, byte[] body) {
		return 1;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Channel channel = ctx.channel();
		ctx.close(new DefaultChannelPromise(ctx.channel()).addListener((ChannelFutureListener) channelFuture ->
				logger.error("exception-"+cause.getMessage()+"导致的channel关闭")));
	}

}
