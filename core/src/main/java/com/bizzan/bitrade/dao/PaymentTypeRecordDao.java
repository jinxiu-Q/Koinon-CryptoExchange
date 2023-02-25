package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.PaymentTypeRecord;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentTypeRecordDao extends BaseDao<PaymentTypeRecord> {

    List<PaymentTypeRecord> findAllByMemberId(Long memberId);

}
