/*
 * Copyright (c) 2017-2018 阿期米德 All Rights Reserved.
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @Date: 2020/3/19 10:51
 * @Version: 1.0
 * History:
 * v1.0.0, sanfeng,  2020/3/19 10:51, Create
 */
package com.bizzan.aqmd.netty.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

/**
 * <p>Description: </p>
 *从ws中解出bytebuf，传给下一次层（pbdecoder），下一层用bytebuf组织成bp
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @Date: 2020/3/19 10:51
 */
public class WebSocketFrameDecoder extends MessageToMessageDecoder<WebSocketFrame> {
    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) {
        ByteBuf buff = msg.content();
        byte[] messageBytes = new byte[buff.readableBytes()];
        buff.readBytes(messageBytes);
        // 直接内存消息
        ByteBuf bytebuf = PooledByteBufAllocator.DEFAULT.buffer();
        // 直接内存
        bytebuf.writeBytes(messageBytes);
        out.add(bytebuf.retain());
    }
}
