package com.bizzan.bitradeline.config;

import com.bizzan.bitradeline.client.Client;
import com.bizzan.bitradeline.service.KlineRobotMarketService;
import com.bizzan.bitradeline.socket.client.WsClientHuobi;
import com.bizzan.bitradeline.util.WebSocketConnectionManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ContractCoinMatchStarter implements ApplicationListener<ContextRefreshedEvent> {

    private Logger log = LoggerFactory.getLogger(ContractCoinMatchStarter.class);

    @Autowired
    private Client client;


    @Autowired
    private KlineRobotMarketService klineRobotMarketService;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        WebSocketConnectionManage.setClient(client);
        WsClientHuobi w = new WsClientHuobi();
        w.setContractMarketService(klineRobotMarketService);
        w.run();
    }
}
