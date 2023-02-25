/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: HawkSubjectContext.java</p>
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
import com.bizzan.aqmd.netty.shiro.util.RequestPairSource;
import org.apache.shiro.subject.SubjectContext;

/**
 * <p>Title: HawkSubjectContext</p>
 * <p>Description: </p>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月25日
 */
public interface HawkSubjectContext extends SubjectContext,RequestPairSource {

	 RequestPacket getHawkRequest();

	 void setHawkRequest(RequestPacket request);

	 RequestPacket resolveHawkRequest();

	 ResponsePacket getHawkResponse();

	 void setHawkResponse(ResponsePacket response);

	 ResponsePacket resolveHawkResponse();
}
