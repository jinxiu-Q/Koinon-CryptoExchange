/*
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: HawkPushServiceImpl.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年8月8日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年8月8日, Create
 */
package com.bizzan.aqmd.netty.push.impl;

import com.bizzan.aqmd.core.common.constant.NettyResponseCode;
import com.bizzan.aqmd.core.entity.ResponsePacket;
import com.bizzan.aqmd.netty.push.HawkPushServiceApi;
import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * Title: HawkPushServiceImpl
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @Date 2020年8月8日
 */
public class HawkPushServiceImpl implements HawkPushServiceApi {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	// 服务端最小序列号
	private final static int MIN_SEQ_ID = 0x1fffffff;
	private static AtomicInteger idWoker = new AtomicInteger(MIN_SEQ_ID);

	@Override
	public void pushMsg(Set<Channel> channels, short cmd, String msg) {
		if(CollectionUtils.isEmpty(channels)){//没有需要推送的用户，直接退出
			return;
		}
		Iterator<Channel> iterable = channels.iterator();
		while(iterable.hasNext()){
			Channel channel = iterable.next();
			try{
				if(channel!=null&&channel.isActive()){
					channel.writeAndFlush(buildResponsePacket(cmd, msg.getBytes()));
				}else{
					logger.debug("推送通道被关闭，移除该推送通道");
					iterable.remove();
					logger.debug("通道移除结果:"+true);
				}
			}catch (Exception e){
				logger.error(e.getMessage(),e);
			}

		};
	}

	@Override
	public void pushMsg(Set<Channel> channels, short cmd, byte[] msg) {
		if(channels==null || CollectionUtils.isEmpty(channels)){//没有需要推送的用户，直接退出
			return;
		}
		try{
			Iterator<Channel> iterable = channels.iterator();
			while(iterable.hasNext()){
				Channel channel = iterable.next();
				if(channel!=null&&channel.isActive()){
					channel.writeAndFlush(buildResponsePacket(cmd, msg));
				}else{
					logger.debug("推送通道被关闭，移除该推送通道");
					iterable.remove();
					logger.debug("通道移除结果:"+true);
				}
			}
		}catch (Exception e){
			logger.error(e.getMessage(),e);
		}
	}

	@Override
	public void pushMsg(Set<Channel> channels, short cmd, MessageLite msg) {
		if(channels==null || CollectionUtils.isEmpty(channels)){//没有需要推送的用户，直接退出
			return;
		}
		try{
			Iterator<Channel> iterable = channels.iterator();
			while(iterable.hasNext()){
				Channel channel = iterable.next();
				if(channel!=null&&channel.isActive()){
					channel.writeAndFlush(buildResponsePacket(cmd, msg.toByteArray()));
				}else{
					logger.debug("推送通道被关闭，移除该推送通道");
					iterable.remove();
					logger.debug("通道移除结果:"+true);
				}
			}
		}catch (Exception e){
			logger.error(e.getMessage(),e);
		}
	}

	@Override
	public Map<String,ChannelPromise> syncPushMsg(Set<Channel> channels, short cmd, String msg) {
		if(CollectionUtils.isEmpty(channels)){//没有需要推送的用户，直接退出
			return null;
		}
		Map<String,ChannelPromise> channelPromiseMap = new HashMap<>();
		Iterator<Channel> iterable = channels.iterator();
		while(iterable.hasNext()){
			Channel channel = iterable.next();
			try{
				if(channel!=null&&channel.isActive()){
					channelPromiseMap.put(channel.id().asLongText(),channel.writeAndFlush(buildResponsePacket(cmd, msg.getBytes())).channel().newPromise());
				}else{
					logger.debug("推送通道被关闭，移除该推送通道");
					iterable.remove();
					logger.debug("通道移除结果:"+true);
				}
			}catch (Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		return channelPromiseMap;
	}

	@Override
	public Map<String,ChannelPromise> syncPushMsg(Set<Channel> channels, short cmd, byte[] msg) {
		if(CollectionUtils.isEmpty(channels)){//没有需要推送的用户，直接退出
			return null;
		}
		Map<String,ChannelPromise> channelPromiseMap = new HashMap<>();
		Iterator<Channel> iterable = channels.iterator();
		while(iterable.hasNext()){
			Channel channel = iterable.next();
			try{
				if(channel!=null&&channel.isActive()){
					channelPromiseMap.put(channel.id().asLongText(),channel.writeAndFlush(buildResponsePacket(cmd, msg)).channel().newPromise());
				}else{
					logger.debug("推送通道被关闭，移除该推送通道");
					iterable.remove();
					logger.debug("通道移除结果:"+true);
				}
			}catch (Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		return channelPromiseMap;
	}

	@Override
	public Map<String,ChannelPromise> syncPushMsg(Set<Channel> channels, short cmd, MessageLite msg) {
		if(CollectionUtils.isEmpty(channels)){//没有需要推送的用户，直接退出
			return null;
		}
		Map<String,ChannelPromise> channelPromiseMap = new HashMap<>();
		Iterator<Channel> iterable = channels.iterator();
		while(iterable.hasNext()){
			Channel channel = iterable.next();
			try {
				if (channel != null && channel.isActive()) {
					channelPromiseMap.put(channel.id().asLongText(), channel.writeAndFlush(buildResponsePacket(cmd, msg.toByteArray())).channel().newPromise());
				} else {
					logger.debug("推送通道被关闭，移除该推送通道");
					iterable.remove();
					logger.debug("通道移除结果:"+true);
				}
			}catch (Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		return channelPromiseMap;
	}

	/**
	 * 
	 * <p>Title: buildResponsePacket</p>
	 * <p>Description: </p>
	 * 构建响应包
	 * @param cmd 指令
	 * @param body 内容
	 * @return 响应内容
	 */
	private ResponsePacket buildResponsePacket(short cmd, byte[] body) {
		ResponsePacket packet = new ResponsePacket();
		packet.setCmd(cmd);
		packet.setSequenceId(nextSeqId());
		packet.setCode(NettyResponseCode.SUCCESS.getResponseCode());
		packet.setBody(body);
		return packet;
	}

	/**
	 * <p>
	 * 唯一序列号。最小值{@link #MIN_SEQ_ID}, 最大值{@link Integer#MAX_VALUE}。区分客户端生成的9位序列号
	 * </p>
	 * 
	 * @return 序列号
	 */
	private static int nextSeqId() {
		int seqId = idWoker.getAndIncrement();
		// AtomicInteger 到达 0x7fffffff后会变为负数
		while (seqId < MIN_SEQ_ID) {
			seqId = idWoker.addAndGet(MIN_SEQ_ID);
		}
		return seqId;
	}
}
