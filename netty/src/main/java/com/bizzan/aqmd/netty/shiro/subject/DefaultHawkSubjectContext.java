/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: DefaultHawkSubjectContext.java</p>
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
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;

/**
 * <p>Title: DefaultHawkSubjectContext</p>
 * <p>Description: </p>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月25日
 */
public class DefaultHawkSubjectContext extends DefaultSubjectContext implements HawkSubjectContext{

	 private static final long serialVersionUID = 8188555355305827739L;

	    private static final String HAWK_REQUEST = DefaultHawkSubjectContext.class.getName() + ".HAWK_REQUEST";
	    private static final String HAWK_RESPONSE = DefaultHawkSubjectContext.class.getName() + ".HAWK_RESPONSE";

	    public DefaultHawkSubjectContext() {
	    }

	    public DefaultHawkSubjectContext(HawkSubjectContext context) {
	        super(context);
	    }

	/* (non-Javadoc)
	 * <p>Title: getHawkRequest</p>
	 * <p>Description: </p>
	 * @return
	 * @see com.spark.hawk.server.shiro.subject.HawkSubjectContext#getHawkRequest()
	 */
	@Override
	public RequestPacket getHawkRequest() {
		return getTypedValue(HAWK_REQUEST, RequestPacket.class);
	}

	/* (non-Javadoc)
	 * <p>Title: setHawkRequest</p>
	 * <p>Description: </p>
	 * @param request
	 * @see com.spark.hawk.server.shiro.subject.HawkSubjectContext#setHawkRequest(RequestPacket)
	 */
	@Override
	public void setHawkRequest(RequestPacket request) {
		if (request != null) {
            put(HAWK_REQUEST, request);
        }
	}

	/* (non-Javadoc)
	 * <p>Title: resolveHawkRequest</p>
	 * <p>Description: </p>
	 * @return
	 * @see com.spark.hawk.server.shiro.subject.HawkSubjectContext#resolveHawkRequest()
	 */
	@Override
	public RequestPacket resolveHawkRequest() {
		RequestPacket request = getHawkRequest();

        //fall back on existing subject instance if it exists:
        if (request == null) {
            Subject existing = getSubject();
            if (existing instanceof HawkSubject) {
                request = ((HawkSubject) existing).getHawkRequest();
            }
        }

        return request;
	}

	/* (non-Javadoc)
	 * <p>Title: getHawkResponse</p>
	 * <p>Description: </p>
	 * @return
	 * @see com.spark.hawk.server.shiro.subject.HawkSubjectContext#getHawkResponse()
	 */
	@Override
	public ResponsePacket getHawkResponse() {
		 return getTypedValue(HAWK_RESPONSE, ResponsePacket.class);
	}

	/* (non-Javadoc)
	 * <p>Title: setHawkResponse</p>
	 * <p>Description: </p>
	 * @param response
	 * @see com.spark.hawk.server.shiro.subject.HawkSubjectContext#setHawkResponse(ResponsePacket)
	 */
	@Override
	public void setHawkResponse(ResponsePacket response) {
		if (response != null) {
            put(HAWK_RESPONSE, response);
        }
	}

	/* (non-Javadoc)
	 * <p>Title: resolveHawkResponse</p>
	 * <p>Description: </p>
	 * @return
	 * @see com.spark.hawk.server.shiro.subject.HawkSubjectContext#resolveHawkResponse()
	 */
	@Override
	public ResponsePacket resolveHawkResponse() {
		ResponsePacket response = getHawkResponse();

        //fall back on existing subject instance if it exists:
        if (response == null) {
            Subject existing = getSubject();
            if (existing instanceof HawkSubject) {
            	response = ((HawkSubject) existing).getHawkResponse();
            }
        }

        return response;
	}

}
