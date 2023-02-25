package com.bizzan.bitrade.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 指数数据
 * @Author: bigdogex.com
 * @Date: 2021-01-06 11:50
 */
@Component
@Slf4j
@ToString
public class IndexData {

    /**
     * 贪婪指数
     */
    @Getter
    @Setter
    private JSONObject sulkindex = null;

    /**
     * 获取贪婪指数（通过非小号的API），每1小时同步一次
     * URL请求返回值
     * {
     * 	"data": {
     * 		"now": 95.000000000000,
     * 		"now_desc": "极度贪婪",
     * 		"yesterday": 93.000000000000,
     * 		"yesterday_desc": "极度贪婪",
     * 		"lastweek": 91.000000000000,
     * 		"lastweek_desc": "极度贪婪",
     * 		"lastmonth": 95.000000000000,
     * 		"lastmonth_desc": "极度贪婪",
     * 		"issuetime": 1609904835,
     * 		"sulks": [{
     * 			"issuetime": 1609862400,
     * 			"sulkindex": 95.000000000000,
     * 			"sulkindex_desc": "极度贪婪"
     *      }]
     *   },
     * 	"code": 200,
     * 	"msg": "success"
     * }
     * @throws UnirestException
     */
    @Scheduled(cron = "0 0 */1 * * *")
    public void sulkindex() throws UnirestException {
        // 非小号接口
        String url = "https://dncapi.bqrank.net/api/v3/discover/sulkindex";

        // 获取贪婪指数
        HttpResponse<JsonNode> resp = Unirest.get(url).asJson();
        if(resp.getStatus() == 200) { // 正确返回
            JSONObject ret = JSON.parseObject(resp.getBody().toString());
            if(ret.getIntValue("code") == 200) { // JSON
                JSONObject result = ret.getJSONObject("data");
                this.setSulkindex(result);
                return;
            }
        }
    }
}
