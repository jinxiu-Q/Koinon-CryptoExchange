package com.bizzan.bitrade.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;

public class ZipUtils {

    /**
     * 解压客户端发来的程序
     *
     * @param depressData
     * @return
     * @throws Exception
     */
    public static byte[] decompress(byte[] depressData) throws Exception {

        ByteArrayInputStream is = new ByteArrayInputStream(depressData);
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        GZIPInputStream gis = new GZIPInputStream(is);

        int count;
        byte data[] = new byte[1024];
        while ((count = gis.read(data, 0, 1024)) != -1) {
            os.write(data, 0, count);
        }
        gis.close();
        depressData = os.toByteArray();
        os.flush();
        os.close();
        is.close();
        return depressData;
    }
}
