package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.Automainconfig;
import org.springframework.stereotype.Repository;


@Repository
public interface AutomainconfigDao extends BaseDao<Automainconfig> {

    Automainconfig findFirstByCoinnameAndProtocol(String coinName, Integer protocol);

}
