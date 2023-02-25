package com.bizzan.bitrade.core;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description: Convert
 * @author: Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @create: 2021/07/04 14:38
 */
public class Convert {
    public Convert() {
    }

    public static int strToInt(String str, int defaultValue) {
        try {
            defaultValue = Integer.parseInt(str);
        } catch (Exception var3) {
            ;
        }

        return defaultValue;
    }

    public static long strToLong(String str, long defaultValue) {
        long l = defaultValue;

        try {
            l = Long.parseLong(str);
        } catch (Exception var6) {
            ;
        }

        return l;
    }

    public static float strToFloat(String str, float defaultValue) {
        try {
            defaultValue = Float.parseFloat(str);
        } catch (Exception var3) {
            ;
        }

        return defaultValue;
    }

    public static double strToDouble(String str, double defaultValue) {
        double d = defaultValue;

        try {
            d = Double.parseDouble(str);
        } catch (Exception var6) {
            ;
        }

        return d;
    }

    public static boolean strToBoolean(String str, boolean defaultValue) {
        try {
            defaultValue = Boolean.parseBoolean(str);
        } catch (Exception var3) {
            ;
        }

        return defaultValue;
    }

    public static Date strToDate(String str, Date defaultValue) {
        return strToDate(str, "yyyy-MM-dd HH:mm:ss", defaultValue);
    }

    public static Date strToDate(String str, String formatStr, Date defaultValue) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);

        try {
            defaultValue = format.parse(str);
        } catch (Exception var5) {
            ;
        }

        return defaultValue;
    }

    public static String dateToStr(Date date, String defaultValue) {
        return dateToStr(date, "yyyy-MM-dd HH:mm:ss", defaultValue);
    }

    public static String dateToStr(Date date, String formatStr, String defaultValue) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);

        try {
            defaultValue = format.format(date);
        } catch (Exception var5) {
            ;
        }

        return defaultValue;
    }

    public static String strToStr(String str, String defaultValue) {
        if (str != null && !str.isEmpty()) {
            defaultValue = str;
        }

        return defaultValue;
    }

    public static java.sql.Date dateToSqlDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    public static Date sqlDateToDate(java.sql.Date date) {
        return new Date(date.getTime());
    }

    public static Timestamp dateToSqlTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }

    public static Date qlTimestampToDate(Timestamp date) {
        return new Date(date.getTime());
    }

    public static int strtoAsc(String st) {
        return st.getBytes()[0];
    }

    public static char intToChar(int backnum) {
        return (char)backnum;
    }
}
