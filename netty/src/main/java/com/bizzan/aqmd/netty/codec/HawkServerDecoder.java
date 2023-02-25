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

import com.bizzan.aqmd.core.entity.RequestPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * <p>Title: HawkServerDecode</p>
 * <p>Description: </p>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年6月26日
 */
public class HawkServerDecoder extends ByteToMessageDecoder {
	 private final static Logger LOGGER = LoggerFactory.getLogger(HawkServerDecoder.class);
	    private Codec codec;
	    public HawkServerDecoder() {
	        this(new DefaultCodec());
	    }

	    public HawkServerDecoder(Codec codec) {
	        this.codec = codec;
	    }

	    @Override
	    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) {
	    	RequestPacket packet = new RequestPacket();
	    	if(byteBuf==null||!ctx.channel().isActive()) {
				return;
			}
	        int packetLen = byteBuf.readInt();
	        LOGGER.debug("原始包长度：{}", packetLen);
	        // 设置序列ID
	        packet.setSequenceId(byteBuf.readLong());
	        // 设置指令代码
	        packet.setCmd(byteBuf.readShort());
	        // 设置指令版本
	        packet.setVersion(byteBuf.readInt());
	        byte[] termByte = new byte[4];
	        byteBuf.readBytes(termByte);
	        packet.setTerminalType(new String(termByte));
	        packet.setRequestId(byteBuf.readInt());
	        byte [] tytes = new byte[byteBuf.readableBytes()];
	        byteBuf.readBytes(tytes);
	        // 解密
	       // packet.setBody(codec.decrypt(ctx.channel(), tytes));
	        packet.setBody(tytes);
	        // 解密后长度
	        packetLen = packet.getLength();
	        LOGGER.debug("解密后包长度：{}", packetLen);
	        // 重置包长度
	        packet.setLength(packetLen);
	        list.add(packet);
	    }
}
