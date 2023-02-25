/*
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: ServerInitializer.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年6月26日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年6月26日, Create
 */
package com.bizzan.aqmd.netty.server;


import com.bizzan.aqmd.core.configuration.NettyProperties;
import com.bizzan.aqmd.netty.codec.Codec;
import com.bizzan.aqmd.netty.codec.HawkServerDecoder;
import com.bizzan.aqmd.netty.codec.HawkServerEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>Title: HawkServerInitializer</p>
 * <p>Description: </p>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @Date 2020年6月26日
 */
public class HawkServerInitializer extends ChannelInitializer<SocketChannel> {
	@Autowired
	private Codec codec;
	@Autowired
    private HawkServerHandler handler;
    @Autowired
    private NettyProperties nettyProperties;

    @Override
    protected void initChannel(SocketChannel socketChannel){
    	LogLevel logLevel;
    	switch(nettyProperties.getServiceLoggerLevel()){
    		case "debug":
    			logLevel=LogLevel.DEBUG;
    			break;
    		case "info":
    			logLevel=LogLevel.INFO;
    			break;
    		case "warn":
    			logLevel=LogLevel.WARN;
    			break;
    		case "error":
    			logLevel=LogLevel.ERROR;
    			break;
    		default:logLevel=LogLevel.ERROR;
    	}
        socketChannel.pipeline()
                .addLast("logger", new LoggingHandler(logLevel))
                .addFirst(new LengthFieldBasedFrameDecoder(nettyProperties.getMaxFrameLength(), 0, nettyProperties.getPacketHeaderLength()
                        , -nettyProperties.getPacketHeaderLength(), 0))
                .addLast("decoder", new HawkServerDecoder(codec))
                .addLast("encoder", new HawkServerEncoder(codec))
                .addLast("idle", new IdleStateHandler(nettyProperties.getReaderIdle(), nettyProperties.getWriterIdle(), nettyProperties.getBothIdle()))
                .addLast("handler", handler)
        ;
    }
}
