/*
 * Copyright (c) 2017-2018 阿期米德 All Rights Reserved.
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @Date: 2020/3/15 14:50
 * @Version: 1.0
 * History:
 * v1.0.0, sanfeng,  2020/3/15 14:50, Create
 */
package com.bizzan.aqmd.netty.common;

import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>Description: </p>
 *
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @Date: 2020/3/15 14:50
 */
public class NettyCacheUtils {
    private  static final Logger logger = LoggerFactory.getLogger(NettyCacheUtils.class);

    //保存某个关键信息和通道的关系，用于按照key进行推送
    private static Map<String, Set<Channel>> channelIdCache = new HashMap<>();
    //保存通道和用户之间的关系，当通道关闭的时候，处理用户对应的channel信息
    public static Map<Channel,String> keyChannelCache = new HashMap<>();
    //保存用户和keys之间的关系
    public static Map<String,Set<String>> userKey = new HashMap<>();
    /**
     *
     * <p>Title: storeChannel</p>
     * <p>Description: </p>
     * 缓存所有的tcp通道信息，只针对登录用户
     * @param key 登陆用户名
     * @param channel 通道信息
     */
    public static void storeChannel(String key, Channel channel) {
        logger.debug("store channel with key:{}, channel id:{}", key, channel.id().asLongText());
        Set<Channel> set = channelIdCache.get(key);
        if(set==null){
            set = new HashSet<>();
            set.add(channel);
            channelIdCache.put(key, set);
        }else if(!set.contains(channel)){
            set.add(channel);
        }

    }
    /**
     *
     * <p>Title: getChannel</p>
     * <p>Description: </p>
     * 根据用户名称获取通道
     * @param key 订阅的key
     * @return 通道集合
     */
    public static Set<Channel> getChannel(String key) {
        if(StringUtils.isEmpty(key)){
            logger.debug("没有订阅[{}]的channel!",key);
        }
        return channelIdCache.get(key);
    }
    public static void removeChannel(String key) {
        if(StringUtils.isEmpty(key)){
            logger.debug("没有订阅[{}]的channel!",key);
        }
        channelIdCache.remove(key);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Set<Channel> getAllChannels(){
        Set<Channel> channels = new HashSet<>();
        channelIdCache.forEach((key,value)->{
            channels.addAll(value);
        });
        return channels;
    }


}