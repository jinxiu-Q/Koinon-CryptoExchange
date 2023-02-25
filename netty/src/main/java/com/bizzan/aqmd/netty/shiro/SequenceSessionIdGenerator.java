/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: SequenceSessionIdGenerator.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月24日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年7月24日, Create
 */
package com.bizzan.aqmd.netty.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Title: SequenceSessionIdGenerator</p>
 * <p>Description: </p>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月24日
 */
public class SequenceSessionIdGenerator implements SessionIdGenerator {
	 // 服务端最小序列号
    private final static int MIN_SEQ_ID = 0x1fffffff;
    private static AtomicInteger idWoker = new AtomicInteger(MIN_SEQ_ID);
	/**
	 * 自定义sessionId实现，使用long类型数据，保证传输为8字节，
	 * 高4位为自动生成，低4位为时间戳的后面4位
	 * 重启以后会重新开始，
	 * 需要注意清空所有的session缓存，否则会有风险
	 */
	@Override
    public Serializable generateId(Session session) {
    	//long result=0;
    	int seqId = idWoker.getAndIncrement();
        // AtomicInteger 到达 0x7fffffff后会变为负数
        while (seqId < MIN_SEQ_ID) {
            seqId = idWoker.addAndGet(MIN_SEQ_ID);
        }
        //result=seqId<<32;
        //long time = System.currentTimeMillis();
        //return result+(int)time;
        return (long)seqId;
    }
//    @SuppressWarnings("static-access")
//	public static void main(String[] args){
//    	long seqId = idWoker.getAndIncrement();
////    	System.out.println(seqId+":"+new Long(seqId).toBinaryString(seqId));
//    	seqId=seqId<<32;
////    	System.out.println(seqId+":"+new Long(seqId).toBinaryString(seqId));
//    	long time = System.currentTimeMillis();
////    	System.out.println(time+":"+new Long(time).toBinaryString(time));
////    	System.out.println(time+":"+new Integer((int)time).toBinaryString((int)time));
//    	seqId= seqId+(int)time;
////    	System.out.println(seqId+":"+new Long(seqId).toBinaryString(seqId));
//        // AtomicInteger 到达 0x7fffffff后会变为负数
//        while (seqId < MIN_SEQ_ID) {
//            seqId = idWoker.addAndGet(MIN_SEQ_ID);
//        }
//        //seqId<<4+(int)(System.currentTimeMillis());
//    }
}
