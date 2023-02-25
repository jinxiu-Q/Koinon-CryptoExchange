/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: HawkSubject.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月25日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年7月25日, Create
 */
package com.bizzan.aqmd.netty.shiro.subject;

import com.bizzan.aqmd.core.entity.RequestPacket;
import com.bizzan.aqmd.core.entity.ResponsePacket;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;

/**
 * <p>
 * Title: HawkSubject
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月25日
 */
public interface HawkSubject extends Subject {
	RequestPacket getHawkRequest();
	ResponsePacket getHawkResponse();
	public static class Builder extends Subject.Builder {
		public Builder(RequestPacket request, ResponsePacket response) {
			this(SecurityUtils.getSecurityManager(), request, response);
		}

		public Builder(SecurityManager securityManager, RequestPacket request, ResponsePacket response) {
			super(securityManager);
			if (request == null) {
				throw new IllegalArgumentException("HawkRequest argument cannot be null.");
			}
			if (response == null) {
				throw new IllegalArgumentException("HawkResponse argument cannot be null.");
			}
			setRequest(request);
			setResponse(response);
		}

		@Override
		protected SubjectContext newSubjectContextInstance() {
			return new DefaultHawkSubjectContext();
		}

		protected Builder setRequest(RequestPacket request) {
			if (request != null) {
				((HawkSubjectContext) getSubjectContext()).setHawkRequest(request);
			}
			return this;
		}

		protected Builder setResponse(ResponsePacket response) {
			if (response != null) {
				((HawkSubjectContext) getSubjectContext()).setHawkResponse(response);
			}
			return this;
		}

		public HawkSubject buildHawkSubject() {
			Subject subject = super.buildSubject();
			if (!(subject instanceof HawkSubject)) {
				String msg = "Subject implementation returned from the SecurityManager was not a "
						+ HawkSubject.class.getName()
						+ " implementation.  Please ensure a Hawk-enabled SecurityManager "
						+ "has been configured and made available to this builder.";
				throw new IllegalStateException(msg);
			}
			return (HawkSubject) subject;
		}
	}
}
