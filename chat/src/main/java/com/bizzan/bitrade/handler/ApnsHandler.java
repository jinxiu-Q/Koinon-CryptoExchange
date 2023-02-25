package com.bizzan.bitrade.handler;

import com.bizzan.bitrade.entity.ChatMessageRecord;
import com.alibaba.fastjson.JSON;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Configuration
public class ApnsHandler {


    @Value("${apns.cert-file-password}")
    private String password;

    @Value("${apns.cert-file-path}")
    private String certFile;

    @Value("${apns.dev-env:true}")
    private Boolean isDevEnv;

    private Map<String, String> uidTokenMap = new HashMap<>();
    //    @Autowired
//    private ApnsService apnsService;
    private Logger logger = LoggerFactory.getLogger(ApnsHandler.class);

    public void setToken(String uid, String token) {
        uidTokenMap.put(uid, token);
    }

    public void removeToken(String uid) {
        uidTokenMap.remove(uid);
    }

    public void handleMessage(String uid, ChatMessageRecord message) {
        logger.info("---------------开始进行apns：" + uid);
        if (StringUtils.isNotEmpty(uid) && uidTokenMap.containsKey(uid)) {
            String token = uidTokenMap.get(uid);
            try {
                logger.info("---------------token" + token);
                sendNotification(token, message);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("发送APNS失败：" + e.getMessage());
            }
        }
    }


    public void sendNotification(String token, ChatMessageRecord message) {

        logger.info("--------message:" + JSON.toJSONString(message));
        ApnsService service = null;
        if (isDevEnv) {
            service = APNS.newService().withCert(certFile, password).withSandboxDestination().build();
        } else {
            service = APNS.newService().withCert(certFile, password).withProductionDestination().build();

        }
        String payloads = APNS.newPayload().alertBody(message.getNameFrom() + ":" + message.getContent()).badge(1)
                .customField("addition", message).sound
                        ("default").build();
        service.push(token, payloads);
        logger.info("------------推送信息已发送！");


//        Payload payload = new Payload();
//        payload.setAlert(message.getNameFrom()+":"+message.getContent());
//        payload.setBadge(1);
//        payload.setSound("default");
//        payload.addParam("addition", JSON.toJSONString(message));
//        PushNotification notification = new PushNotification();
//        notification.setPayload(payload);
//        notification.setToken(token);
//        logger.info("token="+token+",payload="+payload.toString());
//        apnsService.sendNotification(notification);// 异步发送
//        logger.info("---------------apns推送完成");
//        boolean result = apnsService.sendNotificationSynch(notification);// 同步发送
//        logger.info("---------------同步发送："+result);


    }
}
