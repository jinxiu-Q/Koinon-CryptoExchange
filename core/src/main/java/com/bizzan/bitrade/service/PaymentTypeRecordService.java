package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.PaymentTypeRecordDao;
import com.bizzan.bitrade.entity.PaymentTypeRecord;
import com.bizzan.bitrade.service.Base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Hevin E-Mali:390330302@qq.com
 * @description
 * @date 2021/12/29 14:50
 */
@Service
public class PaymentTypeRecordService extends BaseService {
    @Autowired
    private PaymentTypeRecordDao paymentTypeRecordDao;
    @Autowired
    private LocaleMessageSourceService msService;


    public void saveOrUpdate(PaymentTypeRecord record) {
        paymentTypeRecordDao.saveAndFlush(record);
    }

    public List<PaymentTypeRecord> getRecordsByUserId(long userId) {
        return paymentTypeRecordDao.findAllByMemberId(userId);
    }

    public void delRecordById(Long id) {
        paymentTypeRecordDao.delete(id);
    }
}
