package com.bizzan.aqmd.netty.codec;


import io.netty.channel.Channel;

import java.util.Base64;

/**
 * 
 * <p>Title: Base64Codec</p>
 * <p>Description: </p>
 * Base64加密加密
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年6月26日
 */
public class Base64Codec implements Codec {

    @Override
    public byte[] decrypt(Channel channel, byte[] body) {
        return Base64.getDecoder().decode(body);
    }

    @Override
    public byte[] encrypt(Channel channel, byte[] body) {
        return Base64.getEncoder().encode(body);
    }
}
