package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.DataDictionaryDao;
import com.bizzan.bitrade.entity.DataDictionary;
import com.bizzan.bitrade.service.Base.TopBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/1214:19
 */
@Service
public class DataDictionaryService extends TopBaseService<DataDictionary, DataDictionaryDao> {
    @Autowired
    DataDictionaryDao dataDictionaryDao;

    @Override
    @Autowired
    public void setDao(DataDictionaryDao dao) {
        super.setDao(dao);
    }

    public DataDictionary findByBond(String bond) {
        return dataDictionaryDao.findByBond(bond);
    }

}
