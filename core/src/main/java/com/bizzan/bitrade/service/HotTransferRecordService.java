package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.HotTransferRecordDao;
import com.bizzan.bitrade.entity.HotTransferRecord;
import com.bizzan.bitrade.service.Base.TopBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HotTransferRecordService extends TopBaseService<HotTransferRecord,HotTransferRecordDao> {

    @Override
    @Autowired
    public void setDao(HotTransferRecordDao dao) {
        super.setDao(dao);
    }
}
