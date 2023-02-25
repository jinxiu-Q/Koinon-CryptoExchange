package com.bizzan.bitrade;

import com.bizzan.bitrade.service.OrderDetailAggregationService;
import com.bizzan.bitrade.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年03月22日
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=AdminApplication.class)
public class DividendControllerTest {
    @Autowired
    private OrderDetailAggregationService orderDetailAggregationService;
    @Test
    public void queryStatistics(){
        long start = DateUtil.strToDate("2020-03-01 12:30:30").getTime();
        long end = DateUtil.strToDate("2020-03-22 14:30:30").getTime();
        System.out.println("start:"+start+"-----end:"+end);
        orderDetailAggregationService.queryStatistics(start,end);
    }
}
