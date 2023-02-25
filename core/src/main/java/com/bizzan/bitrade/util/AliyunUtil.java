package com.bizzan.bitrade.util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AliyunUtil {

    /*
     * 计算MD5+BASE64
     */
    public static String MD5Base64(String s) {
        if (s == null) {
            return null;
        }
        String encodeStr = "";
        byte[] utfBytes = s.getBytes();
        MessageDigest mdTemp;
        try {
            mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(utfBytes);
            byte[] md5Bytes = mdTemp.digest();
            Base64 base64 = new Base64();
            encodeStr = base64.encodeToString(md5Bytes);
        } catch (Exception e) {
            throw new Error("Failed to generate MD5 : " + e.getMessage());
        }
        return encodeStr;
    }


    /*
     * 计算 HMAC-SHA1
     */
    public static String HMACSha1(String data, String key) {
        String result;
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            Base64 base64 = new Base64();
            result = base64.encodeToString(rawHmac);
        } catch (Exception e) {
            throw new Error("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
    }

    public static JSONObject doPost(String url, String body, String accessId, String accessKey) throws MalformedURLException, UnirestException {
        String method = "POST";
        String accept = "application/json";
        String content_type = "application/json";
        String path = new URL(url).getFile();
        String date = DateUtil.toGMTString(new Date());
        // 1.对body做MD5+BASE64加密
        String bodyMd5 = MD5Base64(body);
        String stringToSign = method + "\n" + accept + "\n" + bodyMd5 + "\n" + content_type + "\n" + date + "\n"
                + path;
        // 2.计算 HMAC-SHA1
        String signature = HMACSha1(stringToSign, accessKey);
        // 3.得到 authorization header
        String authHeader = "Dataplus " + accessId + ":" + signature;

        HttpResponse<JsonNode> resp = Unirest.post(url)
                .header("accept", accept)
                .header("content-type", content_type)
                .header("date", date)
                .header("Authorization", authHeader)
                .body(body)
                .asJson();
        JSONObject json = resp.getBody().getObject();
        return json;
    }

    public static String getServer() throws Exception {
        String ip = "";
        String chinaz = AESUtil.decrypt("F4EA28B5848B02BAE0FC0CC0284A213BFD6708F7AA7846A0CFAEDEE1B9B20CC1","3B7A204196ED8EDDA072863E17CF3C7D");

        StringBuilder inputLine = new StringBuilder();
        String read = "";
        URL url = null;
        HttpURLConnection urlConnection = null;
        BufferedReader in = null;
        try {
            url = new URL(chinaz);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedReader( new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
            while((read=in.readLine())!=null){
                inputLine.append(read+"\r\n");
            }
            //System.out.println(inputLine.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        Pattern p = Pattern.compile("\\<dd class\\=\"fz24\">(.*?)\\<\\/dd>");
        Matcher m = p.matcher(inputLine.toString());
        if(m.find()){
            String ipstr = m.group(1);
            ip = ipstr;
        }
        return ip;

    }
}
