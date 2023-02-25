/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: HawkContext.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月18日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年7月18日, Create
 */
package com.bizzan.aqmd.core.context;

import com.bizzan.aqmd.core.annotation.*;
import com.bizzan.aqmd.core.exception.NettyException;
import com.bizzan.aqmd.core.filter.HFilter;
import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * <p>Title: HawkContext</p>
 * <p>Description: </p>
 * 在spring容器初始化对象的基础之上进行执行，可在对象初始化之前或者之后执行
 * <ul>
 * 	<li>判断对象对应的类上是否声明了HawkBean注解；</li>
 * 	<li>如果声明了HawkBean注解，获取所有声明了HawkMethod注解的方法</li>
 * </ul>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月18日
 */
public class HawkContext implements BeanPostProcessor {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, HawkMethodHandler> hawkMethodHandlerMap;
    private TreeSet<HawkFilterValue> filters;


    public HawkContext() {
        this.hawkMethodHandlerMap = new HashMap<>();
        this.filters = new TreeSet<>();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (AnnotationUtils.findAnnotation(bean.getClass(), HawkBean.class) != null) {
            ReflectionUtils.doWithMethods(bean.getClass(),
                    method -> {

                        //HawkBean 方法的注解
                        HawkMethod hawkMethod = AnnotationUtils.findAnnotation(method, HawkMethod.class);
                        //方法注解上的值
                        HawkMethodValue HawkMethodValue = new HawkMethodValue(hawkMethod.cmd(), hawkMethod.version()
                                , ObsoletedType.isObsoleted(hawkMethod.obsoleted()));
                        //处理方法的类
                        HawkMethodHandler HawkMethodHandler = new HawkMethodHandler();
                        //serviceMethodValue
                        HawkMethodHandler.setHawkMethodValue(HawkMethodValue);
                        //handler
                        HawkMethodHandler.setHandler(bean);
                        //method
                        HawkMethodHandler.setHandlerMethod(method);
                        String handlerKey = buildeHandlerKey(HawkMethodValue.getCmd(), HawkMethodValue.getVersion());
                        //判断重复
                        if (hawkMethodHandlerMap.get(handlerKey) != null) {
                            throw new NettyException(
                                    new StringBuilder("重复的指令， ").append(handlerKey).toString());
                        }
                        // 判断返回类型
                        if (!ClassUtils.isAssignable(MessageLite.class, method.getReturnType())) {
                            throw new NettyException("返回类型只能是MessageLite及其子类");
                        }
                        if (method.getParameterTypes().length > 3) {
                            throw new NettyException(String.format("%s#%s最多包含三个个参数", method.getDeclaringClass().getCanonicalName(), method.getName()));
                        } else if (method.getParameterTypes().length == 1){
                            if (!(ClassUtils.isAssignable(long.class, method.getParameterTypes()[0])
                                    || ClassUtils.isAssignable(byte[].class, method.getParameterTypes()[0])
                                    || ClassUtils.isAssignable(ChannelHandlerContext.class, method.getParameterTypes()[0]))) {
                                throw new NettyException(String.format("允许%s#%s(long)、(byte[])或(ChannelHandlerContext）", method.getDeclaringClass().getCanonicalName(), method.getName()));
                            }
                        } else if(method.getParameterTypes().length == 2){
                            boolean fail = true;
                            if (ClassUtils.isAssignable(long.class, method.getParameterTypes()[0])
                                    && (ClassUtils.isAssignable(byte[].class, method.getParameterTypes()[1])
                                    || ClassUtils.isAssignable(ChannelHandlerContext.class, method.getParameterTypes()[1]))) {
                                fail = false;
                            }
                            if (ClassUtils.isAssignable(byte[].class, method.getParameterTypes()[0])
                                    && (ClassUtils.isAssignable(long.class, method.getParameterTypes()[1])
                                    || ClassUtils.isAssignable(ChannelHandlerContext.class, method.getParameterTypes()[1]))) {
                                fail = false;
                            }
                            if (ClassUtils.isAssignable(ChannelHandlerContext.class, method.getParameterTypes()[0])
                                    && (ClassUtils.isAssignable(long.class, method.getParameterTypes()[1])
                                    || ClassUtils.isAssignable(byte[].class, method.getParameterTypes()[1]))) {
                                fail = false;
                            }
                            if (fail) {
                                throw new NettyException(String.format("允许%s#%s(long, byte[])或(long, ChannelHandlerContext)、(byte[], long)或(byte[], ChannelHandlerContext)、(ChannelHandlerContext, long)或(ChannelHandlerContext, byte[])", method.getDeclaringClass().getCanonicalName(), method.getName()));
                            }
                        }else if(method.getParameterTypes().length == 3){
                            if (!ClassUtils.isAssignable(long.class, method.getParameterTypes()[0])
                                    || !ClassUtils.isAssignable(byte[].class, method.getParameterTypes()[1])
                                    || !ClassUtils.isAssignable(ChannelHandlerContext.class, method.getParameterTypes()[2])) {
                                throw new NettyException(String.format("允许%s#%s(long, byte[], ChannelHandlerContext)", method.getDeclaringClass().getCanonicalName(), method.getName()));
                            }
                        }
                        hawkMethodHandlerMap.put(handlerKey, HawkMethodHandler);
                        logger.info(String.format("注册指令%s", handlerKey));
                    },
                    method -> !method.isSynthetic() && AnnotationUtils.findAnnotation(method, HawkMethod.class) != null
            );
        }
        // 扫描过滤器
        HawkFilter HawkFilter = AnnotationUtils.findAnnotation(bean.getClass(), HawkFilter.class);
        if (HawkFilter != null) {
        	logger.info(String.format("增加过滤器%s", bean.getClass()));
            this.filters.add(new HawkFilterValue(HawkFilter.order(), HawkFilter.cmds(), HawkFilter.ignoreCmds(), (HFilter) bean));
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public HawkMethodHandler getHawkMethodHandler(int cmd, int version) {
        String handlerKey = buildeHandlerKey(cmd, version);
        HawkMethodHandler handler = hawkMethodHandlerMap.get(handlerKey);
        return handler;
    }

    public TreeSet<HawkFilterValue> getFilters() {
        return filters;
    }

    private String buildeHandlerKey(int cmd, int version) {
        return new StringBuilder().append(cmd).append("#").append(version).toString();
    }
}
