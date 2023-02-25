/*
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: HandlerThreadDispatcher.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年8月8日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年8月8日, Create
 */
package com.bizzan.aqmd.netty.server;

import com.bizzan.aqmd.core.Dispatcher;
import com.bizzan.aqmd.core.common.constant.NettyCommands;
import com.bizzan.aqmd.core.configuration.NettyProperties;
import com.bizzan.aqmd.core.entity.Packet;
import com.bizzan.aqmd.core.entity.RequestPacket;
import com.bizzan.aqmd.core.entity.ResponsePacket;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>Title: HandlerThreadDispatcher</p>
 * <p>Description: </p>
 * 接受到客户端发送的请求后，将转码后的对象传递给业务线程，由业务线程进行具体的逻辑处理
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @Date 2020年8月8日
 */
public class HandlerThreadDispatcher {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private  ExecutorService executor;
	@Autowired
	public HandlerThreadDispatcher(NettyProperties nettyProperties){
		executor = Executors.newFixedThreadPool(nettyProperties.getDealHandlerThreadSize());
	}

	public void runByThread(ChannelHandlerContext ctx, RequestPacket msg, Dispatcher<RequestPacket, ResponsePacket> dispatcher){
		try{
			HandlerBusinessDealThread thread = new HandlerBusinessDealThread(ctx,msg,dispatcher);
			executor.submit(thread);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	public class HandlerBusinessDealThread  implements Runnable{
		private Dispatcher<RequestPacket, ResponsePacket> dispatcher;
		private ChannelHandlerContext ctx;
		private RequestPacket packet;
		private HandlerBusinessDealThread(ChannelHandlerContext ctx, RequestPacket packet,Dispatcher<RequestPacket,ResponsePacket> dispatcher){
			this.ctx = ctx;
			this.packet = packet;
			this.dispatcher = dispatcher;
		}	
		@Override
		public void run() {
			Packet response =dispatcher.dispatch(packet, ctx);
	        if (packet.getCmd()!= NettyCommands.HEART_BEAT//心跳请求不进行响应
				&&response != null) {
	            ctx.writeAndFlush(response, 
	            		new DefaultChannelPromise(ctx.channel())
	            		.addListener(
	            				(ChannelFutureListener) channelFuture -> responseComplete(packet))
	            		);
	        }	
		}
		/**
		 * 
		 * <p>Title: responseComplete</p>
		 * <p>Description: </p>
		 * 响应完成事件
		 * @param packet  数据包
		 */
	    private void responseComplete(RequestPacket packet) {
			logger.info("Respone the request of seqId={},cmd={}",packet.getSequenceId(),packet.getCmd());
	    }
	}
}
