package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.CoinprotocolDao;
import com.bizzan.bitrade.dto.CoinprotocolDTO;
import com.bizzan.bitrade.entity.Coinprotocol;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class CoinprotocolService extends BaseService<Coinprotocol> {

    @Autowired
    private CoinprotocolDao coinprotocolDao;

    public List<CoinprotocolDTO> list() {

        return coinprotocolDao.list();

    }

    public Page<Coinprotocol> findAll(Predicate predicate, Pageable pageable) {
        return coinprotocolDao.findAll(predicate, pageable);
    }

    public Coinprotocol findOne(Long id) {
        return coinprotocolDao.findOne(id);
    }

    public Coinprotocol findByProtocol(Integer protocol) {
        return coinprotocolDao.findFirstByProtocol(protocol);
    }

    @Transactional
    public Coinprotocol save(Coinprotocol coinprotocol) {
        return coinprotocolDao.save(coinprotocol);
    }
}
