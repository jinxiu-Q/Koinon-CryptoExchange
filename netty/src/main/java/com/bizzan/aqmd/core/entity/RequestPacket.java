/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: RequestPacket.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月19日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年7月19日, Create
 */
package com.bizzan.aqmd.core.entity;

/**
 * <p>Title: RequestPacket</p>
 * <p>Description: </p>
 * 请求数据包
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月19日
 */
public class RequestPacket  extends Packet{
	protected final static int HEADER_LENGTH = MIN_LENGTH + 8;
    /**
     * 指令版本号，4字节
     */
    private int version;
    /**
     * 终端类型，4字节
     */
    private String terminalType;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
    
    public String getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}

	@Override
    public int getHeaderLength() {
        return HEADER_LENGTH;
    }
}
