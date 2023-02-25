/*
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: HawkRequestDispatcher.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月19日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年7月19日, Create
 */
package com.bizzan.aqmd.netty.dispatcher;

import com.bizzan.aqmd.core.Dispatcher;
import com.bizzan.aqmd.core.annotation.HawkMethodHandler;
import com.bizzan.aqmd.core.common.constant.NettyResponseCode;
import com.bizzan.aqmd.core.context.HawkContext;
import com.bizzan.aqmd.core.entity.HawkResponseMessage;
import com.bizzan.aqmd.core.entity.RequestPacket;
import com.bizzan.aqmd.core.entity.ResponsePacket;
import com.bizzan.aqmd.core.exception.NettyException;
import com.bizzan.aqmd.core.filter.DefaultFilterChain;
import com.bizzan.aqmd.core.filter.FilterChain;
import com.google.common.base.Throwables;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

;

/**
 * <p>
 * Title: HawkRequestDispatcher
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月19日
 */
public class HawkRequestDispatcher implements Dispatcher<RequestPacket, ResponsePacket> {
	private final Logger logger = LoggerFactory.getLogger(HawkRequestDispatcher.class);
	@Autowired
	private HawkContext hawkContext;
	
	@Override
	public ResponsePacket dispatch(RequestPacket request, ChannelHandlerContext ctx) throws NettyException {
		// 获取处理器

		HawkMethodHandler HawkMethodHandler = hawkContext.getHawkMethodHandler(request.getCmd(), request.getVersion());
		ResponsePacket response = new ResponsePacket();
		response.setSequenceId(request.getSequenceId());
		response.setRequestId(request.getRequestId());
		response.setCmd((short) (request.getCmd())); // 终端请求指令+1000返回
		if (HawkMethodHandler == null) {
			logger.error("指令{}#{}不存在", request.getCmd(), request.getVersion());
			response.setCode(NettyResponseCode.CMD_NOT_FOUND.getResponseCode());
			response.setBody(HawkResponseMessage.CommonResult.newBuilder()
					.setResultCode(NettyResponseCode.CMD_NOT_FOUND.getResponseCode())
					.setResultMsg(NettyResponseCode.CMD_NOT_FOUND.getResponseMessage()).build().toByteArray());
			return response;
		}
		if (HawkMethodHandler.getHawkMethodValue().isObsoleted()) {
			logger.error("指令{}#{}已过期", request.getCmd(), request.getVersion());
			response.setCode(NettyResponseCode.OBSOLETED_METHOD.getResponseCode());
			response.setBody(HawkResponseMessage.CommonResult.newBuilder()
					.setResultCode(NettyResponseCode.OBSOLETED_METHOD.getResponseCode())
					.setResultMsg(NettyResponseCode.OBSOLETED_METHOD.getResponseMessage()).build().toByteArray());
			return response;
		}
		return doInvoke(request, ctx, HawkMethodHandler, response);
	}

	private ResponsePacket doInvoke(RequestPacket request, ChannelHandlerContext ctx,
			HawkMethodHandler hawkMethodHandler, ResponsePacket response) {

		try {
			FilterChain chain = new DefaultFilterChain(hawkContext.getFilters(),hawkMethodHandler);
			chain.doFilter(request, response, ctx);
		}catch (RuntimeException e) {//hawkException的内容为：【code-message】格式,可以直接转换成通用返回
			e.printStackTrace();
			logger.error("指令{}#{}业务异常，message={}", request.getCmd(), request.getVersion(),
					e.getMessage());
			response.setCode(NettyResponseCode.REQUEST_ERROR.getResponseCode());
			buildExceptionBody(response,e.getMessage());
		} catch (Exception e) {
			response.setCode(NettyResponseCode.REQUEST_ERROR.getResponseCode());
			if (e instanceof InvalidProtocolBufferException || e.getCause() instanceof InvalidProtocolBufferException) {
				logger.error("指令{}#{}数据包格式错误，{}", request.getCmd(), request.getVersion(),
						Throwables.getStackTraceAsString(e));
				response.setBody(HawkResponseMessage.CommonResult.newBuilder()
					.setResultCode(NettyResponseCode.BODY_FORMAT_ERROR.getResponseCode())
					.setResultMsg(e.getMessage()).build().toByteArray());
			}else{
				logger.error("指令{}#{}未知错误, {}", request.getCmd(), request.getVersion(),
						Throwables.getStackTraceAsString(e),e);
				response.setBody(HawkResponseMessage.CommonResult.newBuilder()
					.setResultCode(NettyResponseCode.UNKNOW_ERROR.getResponseCode())
					.setResultMsg(e.getMessage()).build().toByteArray());
			}
		}
		return response;
	}

	/**
	 * 
	 * <p>Title: buildExceptionBody</p>
	 * <p>Description: </p>
	 * @param response
	 * @param message
	 */
	private void buildExceptionBody(ResponsePacket response,String message){
		String[] results = message.split("~");
		response.setBody(HawkResponseMessage.CommonResult.newBuilder().setResultCode(Integer.parseInt(results[0]))
				.setResultMsg(results[1]).build().toByteArray());
	}
	
}
