package com.bizzan.bitrade.core;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @description: Encrypt
 * @author: Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @create: 2021/07/04 14:45
 */
public class Encrypt {
    private static final String[] hexDigits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    public Encrypt() {
    }

    public static String MD5(String input) {
        return MD5(input, Charset.defaultCharset());
    }

    public static String MD5(String input, String charset) {
        return MD5(input, Charset.forName(charset));
    }

    public static String MD5(String input, Charset charset) {
        MessageDigest digest = null;

        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var4) {
            var4.printStackTrace();
        }

        digest.update(input.getBytes(charset));
        byte[] out = digest.digest();
        return byteToString(out);
    }

    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        if (bByte < 0) {
            iRet = bByte + 256;
        }

        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return hexDigits[iD1] + hexDigits[iD2];
    }

    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();

        for(int i = 0; i < bByte.length; ++i) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }

        return sBuffer.toString();
    }
}
