/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 *
 * <p>FileName: HawkMethodHandler.java</p>
 *
 * Description:
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月18日
 * @version 1.0
 * History:
 */
package com.bizzan.aqmd.core.annotation;


import com.bizzan.aqmd.core.common.constant.NettyResponseCode;
import com.bizzan.aqmd.core.entity.RequestPacket;
import com.bizzan.aqmd.core.entity.ResponsePacket;
import com.bizzan.aqmd.core.exception.NettyException;
import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: HawkMethodHandler</p>
 * <p>Description: </p>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月18日
 */
public class HawkMethodHandler {
	private final Logger logger = LoggerFactory.getLogger(HawkMethodHandler.class);
	//处理器对象
    private Object handler;

    //处理器的处理方法
    private Method handlerMethod;

    // HawkMethod注解值
    private HawkMethodValue hawkMethodValue;

    
    public Object getHandler() {
        return handler;
    }

    public void setHandler(Object handler) {
        this.handler = handler;
    }

    public Method getHandlerMethod() {
        return handlerMethod;
    }

    public void setHandlerMethod(Method handlerMethod) {
        this.handlerMethod = handlerMethod;
    }

	public HawkMethodValue getHawkMethodValue() {
		return hawkMethodValue;
	}

	public void setHawkMethodValue(HawkMethodValue hawkMethodValue) {
		this.hawkMethodValue = hawkMethodValue;
	}

	public Object doInvoke(RequestPacket request, ResponsePacket response, ChannelHandlerContext ctx){

		Class<?>[] paramterTypes = this.getHandlerMethod().getParameterTypes();
		List<Object> params = new ArrayList<>();
		if (paramterTypes.length > 0) {
			for (Class<?> paramterType : paramterTypes) {
				if (ClassUtils.isAssignable(byte[].class, paramterType)) {
					params.add(request.getBody());
				} else if (ClassUtils.isAssignable(ChannelHandlerContext.class, paramterType)) {
					params.add(ctx);
				} else if (ClassUtils.isAssignable(long.class, paramterType)) {
					params.add(request.getSequenceId());
				}

			}
		}
		Object result;
		try {
			result = this.getHandlerMethod().invoke(this.getHandler(),
					params.toArray());
			if(result instanceof String){
				response.setBody(((String)result).getBytes());
			}else{
				MessageLite message = (MessageLite)result;
				// Protobuf 序列化
				response.setBody(message.toByteArray());

			}

			response.setCode(NettyResponseCode.SUCCESS.getResponseCode());

		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(),e);
			throw new NettyException(e, NettyResponseCode.HANDLER_ACCESS_ERROR.getResponseCode()
					+"~"+ NettyResponseCode.HANDLER_ACCESS_ERROR.getResponseMessage());
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage(),e);
			throw new NettyException(e, NettyResponseCode.HANDLER_ARGUMENT_ERROR.getResponseCode()
					+"~"+ NettyResponseCode.HANDLER_ARGUMENT_ERROR.getResponseMessage());
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
            if(cause instanceof NettyException){
                throw new NettyException(cause.getMessage());
            }else{
            	logger.error(NettyResponseCode.HANDLER_INVOCATE_ERROR.getResponseMessage(),e);
    			throw new NettyException(NettyResponseCode.HANDLER_INVOCATE_ERROR.getResponseString());
            }
			
		}
		return result;
	}
}
