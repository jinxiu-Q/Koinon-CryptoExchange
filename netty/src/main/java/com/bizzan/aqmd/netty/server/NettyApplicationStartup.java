/*
 * Copyright (c) 2017-2018 阿期米德 All Rights Reserved.
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @Date: 2020/3/15 10:57
 * @Version: 1.0
 * History:
 * v1.0.0, sanfeng,  2020/3/15 10:57, Create
 */
package com.bizzan.aqmd.netty.server;

import com.bizzan.aqmd.core.configuration.NettyProperties;
import com.bizzan.aqmd.netty.websocket.WebSocketChannelInitializer;
import io.netty.channel.ChannelInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * <p>Description: </p>
 *
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @Date: 2020/3/15 10:57
 */

public class NettyApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        //在容器加载完毕后获取dao层来操作数据库
        NettyProperties nettyProperties = event.getApplicationContext().getBean(NettyProperties.class);
        //在容器加载完毕后获取配置文件中的配置
        ChannelInitializer hawkServerInitializer = event.getApplicationContext().getBean(HawkServerInitializer.class);
        //在容器加载完毕后启动线程
        Thread thread = new Thread(new NettyServer(nettyProperties.getPort(),nettyProperties.getBossThreadSize(),
                nettyProperties.getWorkerThreadSize(),hawkServerInitializer));
        thread.start();
        if(nettyProperties.getWebsocketFlag()==1){//配置了websocket支持,启动websocket服务
            ChannelInitializer webSocketChannelInitializer = event.getApplicationContext().getBean(WebSocketChannelInitializer.class);
            Thread websocketThread = new Thread(new NettyServer(nettyProperties.getWebsocketPort(),nettyProperties.getBossThreadSize(),
                    nettyProperties.getWorkerThreadSize(),webSocketChannelInitializer));
            websocketThread.start();

        }
    }
}