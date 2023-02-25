/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: Hawk.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月18日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年7月18日, Create
 */
package com.bizzan.aqmd.core.annotation;

/**
 * <p>Title: Hawk</p>
 * <p>Description: </p>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月18日
 */
public class HawkMethodValue {
	private int cmd;
    private byte version;
    /**
     * 服务方法是否已经过期,默认不过期
     */
    private boolean obsoleted;

    public HawkMethodValue() {
    }

    public HawkMethodValue(int cmd, byte version, boolean obsoleted) {
        this.cmd = cmd;
        this.version = version;
        this.obsoleted = obsoleted;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public boolean isObsoleted() {
        return obsoleted;
    }

    public void setObsoleted(boolean obsoleted) {
        this.obsoleted = obsoleted;
    }
}
