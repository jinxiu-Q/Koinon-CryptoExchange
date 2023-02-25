/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: Packet.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月2日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年7月2日, Create
 */
package com.bizzan.aqmd.core.entity;

/**
 * <p>Title: Packet</p>
 * <p>Description: </p>
 * Netty和客户端交互的数据包定义
 * <ul>
 * 	<li>包通用信息的声明，包含长度，序列ID，指令码，内容</li>
 * 	<li>长度：默认为包头长度，setBody时重置为包头和body的长度之和</li>
 * 	<li>序列id:对于请求，该值为客户端的唯一id，对于响应，该值为生成的唯一编码</li>
 * 	<li>指令码：需要对方做的具体动作</li>
 * </ul>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月2日
 */
public abstract class Packet {

    protected final static int MIN_LENGTH = 18;

    /**
     * 包长度：4字节每一帧对应的包长度，防止tcp粘包的发生
     */
    private int length;

    /**
     * 客户端唯一的序列ID， 8字节
     */
    private long sequenceId;
    /**
     * 客户端请求id,4字节
     */
    private int requestId;
    /**
     * 指令代码。2字节
     */
    private short cmd;
    /**
     * 包具体内容，protobuffer编码
     */
    private byte[] body;

    public abstract int getHeaderLength();


    public int getLength() {
        if (length == 0) {
            return getHeaderLength();
        }
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public short getCmd() {
        return cmd;
    }

    public void setCmd(short cmd) {
        this.cmd = cmd;
    }

    public byte[] getBody() {
        return body;
    }
    
    public int getRequestId() {
		return requestId;
	}


	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}


	public void setBody(byte [] body) {
        this.body = body;
        if (this.body == null) {
            this.length = getHeaderLength();
        } else {
            this.length = getHeaderLength() + this.body.length;
        }
    }

    public long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(long sequenceId) {
        this.sequenceId = sequenceId;
    }
}
