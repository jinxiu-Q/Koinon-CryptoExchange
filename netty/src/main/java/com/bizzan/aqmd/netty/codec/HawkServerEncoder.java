/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: HawkServerDecode.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年6月26日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年6月26日, Create
 */
package com.bizzan.aqmd.netty.codec;


import com.bizzan.aqmd.core.entity.ResponsePacket;
import com.bizzan.aqmd.core.exception.NettyException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: HawkServerDecode</p>
 * <p>Description: </p>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年6月26日
 */
public class HawkServerEncoder extends MessageToByteEncoder<ResponsePacket>{
	private final static Logger LOGGER = LoggerFactory.getLogger(HawkServerEncoder.class);
    private Codec codec;
    public HawkServerEncoder() {
        this(new DefaultCodec());
    }
    public HawkServerEncoder(Codec codec) {
        this.codec = codec;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ResponsePacket packet, ByteBuf out) throws NettyException {
        LOGGER.debug("原始包长度:{}", packet.getLength());
        // 加密
        byte []  body = codec.encrypt(ctx.channel(), packet.getBody());
        packet.setBody(body);
        LOGGER.debug("加密后包长度:{}", packet.getLength());
        // 写入包长度
        out.writeInt(packet.getLength());
        // 写入序列ID
        out.writeLong(packet.getSequenceId());
        // 写入指令码
        out.writeShort(packet.getCmd());
        // 写入响应码
        out.writeInt(packet.getCode());
        //写入请求Id
        out.writeInt(packet.getRequestId());
        // 写入body
        if (body != null) {
            out.writeBytes(body);
        }
    }
}
