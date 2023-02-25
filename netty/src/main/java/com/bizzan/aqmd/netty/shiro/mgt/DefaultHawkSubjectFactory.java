/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: DefaultHawkSubjectFactory.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月27日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年7月27日, Create
 */
package com.bizzan.aqmd.netty.shiro.mgt;

import com.bizzan.aqmd.core.entity.RequestPacket;
import com.bizzan.aqmd.core.entity.ResponsePacket;
import com.bizzan.aqmd.netty.shiro.subject.HawkSubjectContext;
import com.bizzan.aqmd.netty.shiro.subject.support.HawkDelegatingSubject;
import org.apache.shiro.mgt.DefaultSubjectFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;

/**
 * <p>Title: DefaultHawkSubjectFactory</p>
 * <p>Description: </p>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月27日
 */
public class DefaultHawkSubjectFactory extends DefaultSubjectFactory{
	 public DefaultHawkSubjectFactory() {
	        super();
	    }
		@Override
	    public Subject createSubject(SubjectContext context) {
	        if (!(context instanceof HawkSubjectContext)) {
	            return super.createSubject(context);
	        }
	        HawkSubjectContext wsc = (HawkSubjectContext) context;
	        SecurityManager securityManager = wsc.resolveSecurityManager();
	        Session session = wsc.resolveSession();
	        boolean sessionEnabled = wsc.isSessionCreationEnabled();
	        PrincipalCollection principals = wsc.resolvePrincipals();
	        boolean authenticated = wsc.resolveAuthenticated();
	        String host = wsc.resolveHost();
	        RequestPacket request = wsc.resolveHawkRequest();
	        ResponsePacket response = wsc.resolveHawkResponse();

	        return new HawkDelegatingSubject(principals, authenticated, host, session, sessionEnabled,
	                request, response, securityManager);
	    }

	    /**
	     * @deprecated since 1.2 - override {@link #createSubject(SubjectContext)} directly if you
	     *             need to instantiate a custom {@link Subject} class.
	     */
	    @Deprecated
	    protected Subject newSubjectInstance(PrincipalCollection principals, boolean authenticated,
	                                         String host, Session session,
	                                         RequestPacket request, ResponsePacket response,
	                                         SecurityManager securityManager) {
	        return new HawkDelegatingSubject(principals, authenticated, host, session, true,
	                request, response, securityManager);
	    }
}
