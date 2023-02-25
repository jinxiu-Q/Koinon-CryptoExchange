/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: ShiroFilterFactoryBean.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月25日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年7月25日, Create
 */
package com.bizzan.aqmd.netty.shiro;


import com.bizzan.aqmd.core.filter.HFilter;
import com.bizzan.aqmd.netty.shiro.config.IniFilterChainResolverFactory;
import com.bizzan.aqmd.netty.shiro.filter.AbstractShiroFilter;
import org.apache.shiro.config.Ini;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * Title: ShiroFilterFactoryBean
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月25日
 */
@SuppressWarnings("rawtypes")
public class HawkShiroFilterFactoryBean implements FactoryBean , BeanPostProcessor{
	private static transient final Logger log = LoggerFactory.getLogger(HawkShiroFilterFactoryBean.class);

	private SecurityManager securityManager;
	private Map<String, HFilter> filters;

	private Map<String, String> filterChainDefinitionMap; // urlPathExpression_to_comma-delimited-filter-chain-definition

	private AbstractShiroFilter instance;

	public HawkShiroFilterFactoryBean() {
		this.filters = new LinkedHashMap<String, HFilter>();
		this.filterChainDefinitionMap = new LinkedHashMap<String, String>(); // order
																				// matters!
	}

	public SecurityManager getSecurityManager() {
		return securityManager;
	}

	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}

	@Override
	public Object getObject() throws Exception {
		if (instance == null) {
			instance = createInstance();
		}
		return instance;
	}
	@Override
	public Class getObjectType() {
		return SpringShiroFilter.class;
	}
	@Override
	public boolean isSingleton() {
		return true;
	}

	protected AbstractShiroFilter createInstance() throws Exception {

		log.debug("Creating Shiro Filter instance.");

		SecurityManager securityManager = getSecurityManager();
		if (securityManager == null) {
			String msg = "SecurityManager property must be set.";
			throw new BeanInitializationException(msg);
		}

		// Now create a concrete ShiroFilter instance and apply the acquired
		// SecurityManager and built
		// FilterChainResolver. It doesn't matter that the instance is an
		// anonymous inner class
		// here - we're just using it because it is a concrete
		// AbstractShiroFilter instance that accepts
		// injection of the SecurityManager and FilterChainResolver:
		return new SpringShiroFilter(securityManager);
	}

	public void setFilterChainDefinitions(String definitions) {
		Ini ini = new Ini();
		ini.load(definitions);
		// did they explicitly state a 'urls' section? Not necessary, but just
		// in case:
		Ini.Section section = ini.getSection(IniFilterChainResolverFactory.URLS);
		if (CollectionUtils.isEmpty(section)) {
			// no urls section. Since this _is_ a urls chain definition
			// property, just assume the
			// default section contains only the definitions:
			section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
		}
		setFilterChainDefinitionMap(section);
	}

	public Map<String, HFilter> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, HFilter> filters) {
		this.filters = filters;
	}

	public Map<String, String> getFilterChainDefinitionMap() {
		return filterChainDefinitionMap;
	}

	public void setFilterChainDefinitionMap(Map<String, String> filterChainDefinitionMap) {
		this.filterChainDefinitionMap = filterChainDefinitionMap;
	}

	private static final class SpringShiroFilter extends AbstractShiroFilter {

		protected SpringShiroFilter(SecurityManager webSecurityManager) {
			super();
			if (webSecurityManager == null) {
				throw new IllegalArgumentException("WebSecurityManager property cannot be null.");
			}
			setSecurityManager(webSecurityManager);
		}
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
}
