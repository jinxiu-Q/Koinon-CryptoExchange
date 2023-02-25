package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.DataDictionary;

/**
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/1214:15
 */
public interface DataDictionaryDao extends BaseDao<DataDictionary> {
    DataDictionary findByBond(String bond);
}
