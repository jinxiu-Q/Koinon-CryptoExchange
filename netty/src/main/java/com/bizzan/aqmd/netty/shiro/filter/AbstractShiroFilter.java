/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: AbstractShiroFilter.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月25日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年7月25日, Create
 */
package com.bizzan.aqmd.netty.shiro.filter;

import com.bizzan.aqmd.core.entity.RequestPacket;
import com.bizzan.aqmd.core.entity.ResponsePacket;
import com.bizzan.aqmd.core.exception.NettyException;
import com.bizzan.aqmd.core.filter.FilterChain;
import com.bizzan.aqmd.core.filter.HFilter;
import com.bizzan.aqmd.netty.shiro.subject.HawkSubject;
import io.netty.channel.ChannelHandlerContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.ExecutionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * <p>
 * Title: AbstractShiroFilter
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月25日
 */
public abstract class AbstractShiroFilter extends HFilter {
	private static final Logger log = LoggerFactory.getLogger(AbstractShiroFilter.class);

	// Reference to the security manager used by this filter
	private SecurityManager securityManager;

	protected Subject createSubject(RequestPacket request, ResponsePacket response) {
		return new HawkSubject.Builder(getSecurityManager(), request, response).buildHawkSubject();
	}

	protected void updateSessionLastAccessTime(RequestPacket request, ResponsePacket response) {
		Subject subject = SecurityUtils.getSubject();
		// Subject should never _ever_ be null, but just in case:
		if (subject != null) {
			ThreadContext.bind(subject);
			Session session = subject.getSession(false);
			if (session != null) {
				try {
					session.touch();
				} catch (Throwable t) {
					log.error("session.touch() method invocation has failed.  Unable to update"
							+ "the corresponding session's last access time based on the incoming request.", t);
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void doFilterInternal(RequestPacket request, ResponsePacket response,ChannelHandlerContext ctx, FilterChain chain) throws NettyException, IOException {

		Throwable t = null;

		try {
			final Subject subject = createSubject(request, response);
			// noinspection unchecked
			subject.execute(new Callable() {
				@Override
				public Object call() throws Exception {
					updateSessionLastAccessTime(request, response);
					chain.doFilter(request, response, ctx);
					return null;
				}
			});
		} catch (ExecutionException ex) {
			t = ex.getCause();
		} catch (Throwable throwable) {
			t = throwable;
		}

		if (t != null) {
			if (t instanceof NettyException) {
				throw (NettyException) t;
			}
			if (t instanceof IOException) {
				throw (IOException) t;
			}
			log.error(t.getMessage(), t);
			// otherwise it's not one of the two exceptions expected by the
			// filter method signature - wrap it in one:
			String msg = "Filtered request failed.";
			throw new NettyException(t, msg);
		}
	}
	@Override
	public final void doFilter(RequestPacket request, ResponsePacket response,ChannelHandlerContext ctx, FilterChain chain ) throws NettyException, IOException {
		doFilterInternal(request, response,ctx,chain);
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {
	}

	public SecurityManager getSecurityManager() {
		return securityManager;
	}

	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}

}
