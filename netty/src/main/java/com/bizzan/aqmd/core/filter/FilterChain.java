/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: FilterChain.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月28日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年7月28日, Create
 */
package com.bizzan.aqmd.core.filter;

import com.bizzan.aqmd.core.entity.RequestPacket;
import com.bizzan.aqmd.core.entity.ResponsePacket;
import io.netty.channel.ChannelHandlerContext;

/**
 * <p>Title: FilterChain</p>
 * <p>Description: </p>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月28日
 */
public interface FilterChain {
	
	public void doFilter(RequestPacket request, ResponsePacket response, ChannelHandlerContext ctx);
}
