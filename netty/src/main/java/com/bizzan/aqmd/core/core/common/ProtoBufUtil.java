/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: ProtoBufUtil.java</p>
 * 
 * Description: 
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月2日
 * @version 1.0
 * History:
 * v1.0.0, , 2020年7月2日, Create
 */
package com.bizzan.aqmd.core.core.common;

import com.bizzan.aqmd.core.entity.RequestPacket;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>Title: ProtoBufUtil</p>
 * <p>Description: </p>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年7月2日
 */
public class ProtoBufUtil {
	public ProtoBufUtil() {  
    }  
  
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> byte[] serializer(T o) {  
        Schema schema = RuntimeSchema.getSchema(o.getClass());
        return ProtobufIOUtil.toByteArray(o, schema, LinkedBuffer.allocate(256));
    }  
  
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T deserializer(byte[] bytes, Class<T> clazz) {  
  
        T obj = null;  
        try {  
            obj = clazz.newInstance();  
            Schema schema = RuntimeSchema.getSchema(obj.getClass());  
            ProtostuffIOUtil.mergeFrom(bytes, obj, schema);  
        } catch (InstantiationException e) {  
            e.printStackTrace();  
        } catch (IllegalAccessException e) {  
            e.printStackTrace();  
        }  
  
        return obj;  
    }
    public  static byte[] buildRequestBytes(String terminal,short cmd,int version,long sequenceId,byte[] body) throws IOException {
        RequestPacket packet = new RequestPacket();
        packet.setCmd(cmd);
        packet.setVersion(version);
        packet.setBody(body);
        //packet.setSequenceId(sequenceId);
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        int length =packet.getLength();
        dos.writeInt(length);
        dos.writeLong(sequenceId);
        dos.writeShort(cmd);
        dos.writeInt(version);
        byte[] terminalBytes = terminal.getBytes();
        dos.write(terminalBytes);
        dos.writeInt(0);//requestId
        if(body!=null) {
            dos.write(body);
        }
        return bos.toByteArray();
    }
}
