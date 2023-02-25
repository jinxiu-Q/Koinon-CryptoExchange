/*
 * Copyright (c) 2008-2015 浩瀚深度 All Rights Reserved.
 * 
 * <p>FileName: SpringContextUtils.java</p>
 * 
 * @author fanqiong
 * @date 2020年3月23日
 * @version 1.0
 * History:
 * v1.0.0, 范琼, 2020年3月23日, Create
 */
package com.bizzan.aqmd.core.core.common;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * <p>Title: SpringContextUtils</p>
 * <p>Description: </p>
 * <p>Spring的工具类，用来获取容器中的bean</p>
 * @author fanqiong
 * @date 2020年3月23日
 */
public class NettySpringContextUtils implements ApplicationContextAware {
	
	private static ApplicationContext applicationContext = null;  

	/* (non-Javadoc)
	 * <p>Title: setApplicationContext</p>
	 * <p>Description: </p>
	 * @param applicationContext
	 * @throws BeansException
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 * 当继承了ApplicationContextAware类之后，程序在调用getBean(String)的时候会自动调用该方法，不用自己操作 
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        NettySpringContextUtils.applicationContext = applicationContext;
	}
	
	public static ApplicationContext getApplicationContext() {  
        return applicationContext;  
    }  
    /*** 
     * 根据一个bean的id获取配置文件中相应的bean 
     * @param name 
     * @return 
     * @throws BeansException 
     */  
    public static Object getBean(String name) throws BeansException {  
        return applicationContext.getBean(name);  
    } 
    
    /*** 
     * 类似于getBean(String name)只是在参数中提供了需要返回到的类型。 
     * @param name 
     * @param requiredType 
     * @return 
     * @throws BeansException 
     */  
	public static Object getBean(String name, Class requiredType) throws BeansException {  
        return applicationContext.getBean(name, requiredType);  
    }  
           
          /** 
          * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true  
          * @param name 
          * @return boolean 
          */  
    public static boolean containsBean(String name) {  
         return applicationContext.containsBean(name);  
    }  
           
          /** 
          * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。 
          * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）    
          * @param name 
          * @return boolean 
          * @throws NoSuchBeanDefinitionException 
          */  
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {  
          return applicationContext.isSingleton(name);  
    }  
           
          /** 
          * @param name 
          * @return Class 注册对象的类型 
          * @throws NoSuchBeanDefinitionException 
          */  
	public static Class getType(String name) throws NoSuchBeanDefinitionException {  
         return applicationContext.getType(name);  
    }  
           
          /** 
          * 如果给定的bean名字在bean定义中有别名，则返回这些别名    
          * @param name 
          * @return 
          * @throws NoSuchBeanDefinitionException 
          */  
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {  
         return applicationContext.getAliases(name);  
    }  

}
