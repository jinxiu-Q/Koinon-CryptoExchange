package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.MemberRecord;

import java.util.List;

/**
 * @Author: bigdogex.com
 * @Date: 2021-01-20 12:30
 */
public interface MemberRecordDao extends BaseDao<MemberRecord> {
    List<MemberRecord> findAllByAnalysised(int analysised);
}
