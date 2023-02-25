/*
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: HawkServerHandler.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年6月26日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年6月26日, Create
 */
package com.bizzan.aqmd.netty.server;

import com.bizzan.aqmd.core.entity.RequestPacket;
import com.bizzan.aqmd.netty.dispatcher.HawkRequestDispatcher;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;

/**
 * <p>Title: HawkServerHandler</p>
 * <p>Description: </p>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @Date 2020年6月26日
 */
public abstract class HawkServerHandler extends SimpleChannelInboundHandler<RequestPacket>{
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private HandlerThreadDispatcher threadDispatcher;
	@Autowired
	private HawkRequestDispatcher dispatcher;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RequestPacket packet){
    	InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        logger.info("Receive Request from {} of {}, cmd={}, version={}, seqId={}"
                , remoteAddress.getAddress().getHostAddress(), ctx.channel().id().asLongText()
                , packet.getCmd(), packet.getVersion(), packet.getSequenceId()
        );
        threadDispatcher.runByThread(ctx, packet,dispatcher);
	}


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx){
        ctx.disconnect(ctx.newPromise());
//        channels.remove(ctx.channel());
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
    }
}
