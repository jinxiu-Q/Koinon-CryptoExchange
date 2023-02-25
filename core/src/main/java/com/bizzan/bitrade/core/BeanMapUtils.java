package com.bizzan.bitrade.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: BeanMapUtils
 * @author: Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @create: 2021/07/04 14:44
 */
public class BeanMapUtils {
    public static Log log = LogFactory.getLog(BeanMapUtils.class);
    public static String format = "yyyy-MM-dd HH:mm:ss";

    public BeanMapUtils() {
    }

    public static void map2Bean(Map<String, ?> map, Object bean) throws NumberFormatException, IllegalArgumentException, IllegalAccessException {
        Class<?> rtClass = bean.getClass();
        Field[] fields = rtClass.getDeclaredFields();
        Field[] var7 = fields;
        int var6 = fields.length;

        for(int var5 = 0; var5 < var6; ++var5) {
            Field field = var7[var5];
            field.setAccessible(true);
            String key = field.getName();
            if (field.isAnnotationPresent(com.bizzan.bitrade.core.Field.class)) {
                com.bizzan.bitrade.core.Field alias = (com.bizzan.bitrade.core.Field)field.getAnnotation(com.bizzan.bitrade.core.Field.class);
                key = alias.value();
            }

            Class<?> type = field.getType();
            Object value = map.get(key);
            if (value != null) {
                String typeName = type.getName();
                if (typeName.equals("int")) {
                    field.setInt(bean, Integer.parseInt(value.toString()));
                } else if (typeName.equals("long")) {
                    field.setLong(bean, Long.parseLong(value.toString()));
                } else if (typeName.equals("double")) {
                    field.setDouble(bean, Double.parseDouble(value.toString()));
                } else {
                    field.set(bean, value);
                }
            }
        }

    }

    public static Map<String, String> bean2Map(Object bean) throws IllegalArgumentException, IllegalAccessException {
        Class<?> rtClass = bean.getClass();
        Field[] fields = rtClass.getDeclaredFields();
        Map<String, String> map = new HashMap();
        Field[] var7 = fields;
        int var6 = fields.length;

        for(int var5 = 0; var5 < var6; ++var5) {
            Field field = var7[var5];
            field.setAccessible(true);
            String key = field.getName();
            if (field.isAnnotationPresent(com.bizzan.bitrade.core.Field.class)) {
                com.bizzan.bitrade.core.Field alias = (com.bizzan.bitrade.core.Field)field.getAnnotation(com.bizzan.bitrade.core.Field.class);
                key = alias.value();
            }

            Object value = field.get(bean);
            if (value != null) {
                map.put(key, value.toString());
            }
        }

        return map;
    }
}
