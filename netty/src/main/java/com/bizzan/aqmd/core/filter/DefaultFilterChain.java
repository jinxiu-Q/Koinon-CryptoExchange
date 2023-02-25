/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: DefaultFilterChain.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月28日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年7月28日, Create
 */
package com.bizzan.aqmd.core.filter;


import com.bizzan.aqmd.core.annotation.HawkFilterValue;
import com.bizzan.aqmd.core.annotation.HawkMethodHandler;
import com.bizzan.aqmd.core.common.constant.NettyResponseCode;
import com.bizzan.aqmd.core.entity.RequestPacket;
import com.bizzan.aqmd.core.entity.ResponsePacket;
import com.bizzan.aqmd.core.exception.NettyException;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.collections.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * <p>Title: DefaultFilterChain</p>
 * <p>Description: </p>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月28日
 */
public class DefaultFilterChain implements FilterChain {
	
	private List<HawkFilterValue> filters = new ArrayList<HawkFilterValue>();
	private HawkMethodHandler handler;
	private int _iter=0;
	private boolean handlerExecFlag = false;

	public DefaultFilterChain(  TreeSet<HawkFilterValue> treeFilters,HawkMethodHandler handler) {
		this.handler = handler;
		for (HawkFilterValue filterValue : treeFilters) {
			filters.add(filterValue);
		}
	}

	@Override
	public void doFilter(RequestPacket request, ResponsePacket response, ChannelHandlerContext ctx) {
		if(CollectionUtils.isEmpty(filters)){
			if(handler==null){
				throw new NettyException(NettyResponseCode.NO_HANDLER_ERROR.getResponseCode()
						+"~"+ NettyResponseCode.NO_HANDLER_ERROR.getResponseMessage());
			}
			handler.doInvoke(request, response, ctx);
			handlerExecFlag=true;
			return;
		}
		//迭代器将所有的过滤器都执行完成，执行handler
		try {
			HawkFilterValue filterValue;
			//找到下一个符合拦截条件的过滤器
			for(;_iter<filters.size();_iter++){
				if((filterValue = filters.get(_iter)).getHfilter().isMatch(request)){
					//匹配到过滤器，执行并在执行完后退出循环
					_iter++;//当前过滤器已经获取到，加1保证下一轮能获取到下一个过滤器
					HFilter hFilter = filterValue.getHfilter();
					hFilter.doFilter(request, response,ctx, this);
					break;//找到过滤器并执行以后就不需要继续循环
				}
			}
			
			if(_iter==filters.size() && !handlerExecFlag){//所有的过滤器都遍历完还没有适用的，执行handler
				handler.doInvoke(request, response, ctx);
				handlerExecFlag=true;
			}
		} catch ( IOException e) {
			throw new NettyException(NettyResponseCode.FILTER_IO_ERROR.getResponseCode()
					+"~"+e.getMessage());
		} catch ( RuntimeException e) {
			throw e;
		} catch ( Exception e) {
			throw new NettyException(NettyResponseCode.UNKNOW_ERROR.getResponseCode()
					+"~"+ NettyResponseCode.UNKNOW_ERROR.getResponseMessage());
		}
	}
}
