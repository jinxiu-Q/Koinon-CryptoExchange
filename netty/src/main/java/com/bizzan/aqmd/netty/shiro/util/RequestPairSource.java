/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: RequestPairSource.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月25日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年7月25日, Create
 */
package com.bizzan.aqmd.netty.shiro.util;


import com.bizzan.aqmd.core.entity.RequestPacket;
import com.bizzan.aqmd.core.entity.ResponsePacket;

/**
 * <p>Title: RequestPairSource</p>
 * <p>Description: </p>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月25日
 */
public interface RequestPairSource {

	RequestPacket getHawkRequest();

	ResponsePacket getHawkResponse();
}
