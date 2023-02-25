package com.bizzan.bitrade.controller;

import com.alibaba.fastjson.JSON;
import com.bizzan.bitrade.entity.PaymentType;
import com.bizzan.bitrade.entity.PaymentTypeRecord;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.service.PaymentTypeRecordService;
import com.bizzan.bitrade.service.PaymentTypeService;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vo.PaymentTypeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;

/**
 * @author Hevin E-Mali:390330302@qq.com
 * @Description: coin
 * @date 2021/4/214:20
 */
@RestController
@RequestMapping("payment")
public class PaymentController extends BaseController {
    @Autowired
    private PaymentTypeService paymentTypeService;
    @Autowired
    private PaymentTypeRecordService paymentTypeRecordService;

    private String[] colors = {"#f0a70a","#e5dc2a","#4fbe51","#d07e3b","#0a4bf0","#810af0","#2b9f76"};
    // 查询所有支付方式
    @GetMapping("list")
    public MessageResult list() {
        List<PaymentType> list = paymentTypeService.findAll();
        return success(list);
    }
    // 查询支付方式配置
    @GetMapping("findPaymentTypeConfigById")
    public MessageResult list(@RequestParam(value = "id") Long id) {
        List<PaymentTypeConfig> list = paymentTypeService.findPaymentTypeConfigById(id);
        return success(list);
    }

    /**
     * 绑定支付方式
     */
    @RequestMapping("saveOrUpdate")
    public MessageResult saveOrUpdate(@SessionAttribute(SESSION_MEMBER) AuthMember user,PaymentTypeRecord record) {
        record.setMemberId(user.getId());
        paymentTypeRecordService.saveOrUpdate(record);
        return success();
    }

    /**
     * 绑定支付方式
     */
    @RequestMapping("getRecords")
    public MessageResult getRecords(@SessionAttribute(SESSION_MEMBER) AuthMember user) {
       List<PaymentTypeRecord> list = paymentTypeRecordService.getRecordsByUserId(user.getId());
       if(list!=null && list.size()>0) {
           int index = 0;
           for (PaymentTypeRecord record : list) {
               PaymentType type = paymentTypeService.findPaymentTypeById(record.getType());
               record.setTypeName(type.getCode());
               List<PaymentTypeConfig> cList = JSON.parseArray(type.getConfigJson(),PaymentTypeConfig.class);
               Map<String, String> fieldType = cList.stream().collect(Collectors.toMap(PaymentTypeConfig::getFieldName, PaymentTypeConfig::getType));
               record.setFieldType(fieldType);
               record.setColor(colors[index%6]);
               index++;
           }
       }
       return success(list);
    }

    /**
     * 绑定支付方式
     */
    @RequestMapping("delRecordById")
    public MessageResult delRecordById(@SessionAttribute(SESSION_MEMBER) AuthMember user,@RequestParam("id") Long id) {
        paymentTypeRecordService.delRecordById(id);
        return success();
    }


}
