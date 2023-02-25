/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: HawkDelegatingSubject.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月27日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年7月27日, Create
 */
package com.bizzan.aqmd.netty.shiro.subject.support;

import com.bizzan.aqmd.core.entity.RequestPacket;
import com.bizzan.aqmd.core.entity.ResponsePacket;
import com.bizzan.aqmd.netty.shiro.mgt.HawkSessionContext;
import com.bizzan.aqmd.netty.shiro.session.DefaultHawkSessionContext;
import com.bizzan.aqmd.netty.shiro.subject.HawkSubject;
import com.bizzan.aqmd.netty.shiro.util.HawkUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.support.DelegatingSubject;
import org.apache.shiro.util.StringUtils;

/**
 * <p>Title: HawkDelegatingSubject</p>
 * <p>Description: </p>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月27日
 */
public class HawkDelegatingSubject extends DelegatingSubject implements HawkSubject {

    @SuppressWarnings("unused")
	private static final long serialVersionUID = -1655724323350159250L;

    private final RequestPacket requestPacket;
    private final ResponsePacket responsePacket;

    public HawkDelegatingSubject(PrincipalCollection principals, boolean authenticated,
                                String host, Session session,
                                RequestPacket request, ResponsePacket response,
                                SecurityManager securityManager) {
        this(principals, authenticated, host, session, true, request, response, securityManager);
    }

    //since 1.2
    public HawkDelegatingSubject(PrincipalCollection principals, boolean authenticated,
                                String host, Session session, boolean sessionEnabled,
                                RequestPacket request, ResponsePacket response,
                                SecurityManager securityManager) {
        super(principals, authenticated, host, session, sessionEnabled, securityManager);
        this.requestPacket = request;
        this.responsePacket = response;
    }

   
    @Override
    public RequestPacket getHawkRequest() {
		return requestPacket;
	}
    @Override
	public ResponsePacket getHawkResponse() {
		return responsePacket;
	}

	/**
     * Returns {@code true} if session creation is allowed  (as determined by the super class's
     * {@link super#isSessionCreationEnabled()} value and no request-specific override has disabled sessions for this subject,
     * {@code false} otherwise.
     * <p/>
     * This means session creation is disabled if the super {@link super#isSessionCreationEnabled()} property is {@code false}
     * or if a request attribute is discovered that turns off sessions for the current request.
     *
     * @return {@code true} if session creation is allowed  (as determined by the super class's
     *         {@link super#isSessionCreationEnabled()} value and no request-specific override has disabled sessions for this
     *         subject, {@code false} otherwise.
     * @since 1.2
     */
    @Override
    protected boolean isSessionCreationEnabled() {
        boolean enabled = super.isSessionCreationEnabled();
        return enabled && HawkUtils._isSessionCreationEnabled(this);
    }

    @Override
    protected SessionContext createSessionContext() {
        HawkSessionContext hsc = new DefaultHawkSessionContext();
        String host = getHost();
        if (StringUtils.hasText(host)) {
        	hsc.setHost(host);
        }
        hsc.setHawkRequest(this.requestPacket);
        hsc.setHawkResponse(this.responsePacket);
        return hsc;
    }

}
