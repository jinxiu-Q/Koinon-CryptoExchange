package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.AutomainconfigDao;
import com.bizzan.bitrade.entity.Automainconfig;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutomainconfigService extends BaseService<Automainconfig> {

    @Autowired
    private AutomainconfigDao automainconfigDao;

    public Automainconfig findOne(String coinName, Integer protocol) {
        return automainconfigDao.findFirstByCoinnameAndProtocol(coinName, protocol);
    }

    public Page<Automainconfig> findAll(Predicate predicate, Pageable pageable) {
        return automainconfigDao.findAll(predicate, pageable);
    }

    @Transactional
    public Automainconfig save(Automainconfig automainconfig) {
        return automainconfigDao.save(automainconfig);
    }

}
