package com.bizzan.aqmd.netty.codec;

import io.netty.channel.Channel;

/**
 * 
 * <p>Title: DefaultCodec</p>
 * <p>Description: </p>
 *  *     缺省加解密实现
 * <ol>
 *     <li>解码不解密</li>
 *     <li>编码不加密</li>
 * </ol>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年6月26日
 */
public class DefaultCodec implements Codec {

    @Override
    public byte[] decrypt(Channel channel, byte[] body) {
        // 不解密
        return body;
    }

    @Override
    public byte[] encrypt(Channel channel, byte[] body) {
        // 不解密
        return body;
    }
}
