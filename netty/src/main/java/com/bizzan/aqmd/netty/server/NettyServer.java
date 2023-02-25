/*
 * Copyright (c) 2017-2018  All Rights Reserved.
 *
 * <p>FileName: NettyServer.java</p>
 *
 * Description:
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年6月26日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年6月26日, Create
 */
package com.bizzan.aqmd.netty.server;

import com.bizzan.aqmd.core.exception.NettyException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * <p>
 * Title: NettyServer
 * </p>
 * <p>
 * Description:
 * </p>
 *
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @Date 2020年6月26日
 */
public class NettyServer implements Server, Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private int port ;
    private int bossThreadSize;
    private int workerThreadSize;
    private ChannelInitializer channelInitializer;

    NettyServer(int port ,int bossThreadSize,int workerThreadSize, ChannelInitializer channelInitializer) {
        this.port = port;
        this.bossThreadSize = bossThreadSize;
        this.workerThreadSize = workerThreadSize;
        this.channelInitializer = channelInitializer;
    }

    @Override
    public void open() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(bossThreadSize);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(workerThreadSize);
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                // 设置链接缓冲池的大小
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_RCVBUF, 1024 * 1024)
                .childOption(ChannelOption.SO_SNDBUF, 10 * 1024 * 1024)
                // 设置维持链接的活跃，清除死链接
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                //设置内存对象池
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                // 设置关闭延迟发送
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(this.channelInitializer);
        InetSocketAddress localAddress = new InetSocketAddress(port);
        try {
            // 绑定端口，同步等待
            ChannelFuture f = b.bind(localAddress).sync();
            logger.info("Server started at port {}", localAddress.getPort());
            Channel serverChannel = f.channel();
            // 等待监听端口关闭
            serverChannel.closeFuture().sync();
        } catch (InterruptedException e) {

            throw new NettyException(e.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void run() {
        this.open();
    }

    /*
     * (non-Javadoc) <p>Title: close</p> <p>Description: </p>
     *
     * @see com.spark.hawk.server.Server#close()
     */
    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc) <p>Title: isClosed</p> <p>Description: </p>
     *
     * @return
     *
     * @see com.spark.hawk.server.Server#isClosed()
     */
    @Override
    public boolean isClosed() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc) <p>Title: isAvailable</p> <p>Description: </p>
     *
     * @return
     *
     * @see com.spark.hawk.server.Server#isAvailable()
     */
    @Override
    public boolean isAvailable() {
        // TODO Auto-generated method stub
        return false;
    }


}
