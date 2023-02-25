package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.entity.HistoryChatMessage;
import com.bizzan.bitrade.entity.HistoryMessagePage;
import com.bizzan.bitrade.handler.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HistoryMessageController {

    @Autowired
    private MessageHandler chatMessageHandler ;

    @RequestMapping("/getHistoryMessage")
    public HistoryMessagePage getHistoryMessage(HistoryChatMessage message){
        return chatMessageHandler.getHistoryMessage(message);
    }
}
