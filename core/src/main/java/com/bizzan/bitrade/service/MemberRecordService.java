package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.MemberRecordDao;
import com.bizzan.bitrade.entity.MemberRecord;
import com.bizzan.bitrade.service.Base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: bigdogex.com
 * @Date: 2021-01-20 12:31
 */
@Service
public class MemberRecordService extends BaseService {
    @Autowired
    private MemberRecordDao memberRecordDao;

    public MemberRecord save(MemberRecord memberRecord) {
        return memberRecordDao.save(memberRecord);
    }
    public List<MemberRecord> findALlByAnalysised(int analysised){
        return memberRecordDao.findAllByAnalysised(analysised);
    }
}
