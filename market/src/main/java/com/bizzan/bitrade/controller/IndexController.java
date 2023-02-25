package com.bizzan.bitrade.controller;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.component.IndexData;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 指数API
 * @Author: bigdogex.com
 * @Date: 2021-01-06 12:06
 */
@Slf4j
@RestController
@RequestMapping("indexdata")
public class IndexController {
    @Autowired
    private IndexData indexData;

    @RequestMapping("sulkindex")
    public JSONObject sulkIndex() throws UnirestException {
        JSONObject retData =  indexData.getSulkindex();
        if(retData == null) {
            indexData.sulkindex();
            retData =  indexData.getSulkindex();
        }
        return retData;
    }
}
