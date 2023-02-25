package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.AddressextDao;
import com.bizzan.bitrade.entity.Addressext;
import com.bizzan.bitrade.service.Base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AddressextService extends BaseService {

    @Autowired
    private AddressextDao addressextDao;

    public Addressext read(Integer memberid, Integer coinprotocol) {
        return addressextDao.findFirstByMemberIdAndCoinProtocol(memberid, coinprotocol);
    }

    public Addressext findByAddress(String address) {
        return addressextDao.findFirstByAddressAndStatus(address,1);
    }

    // 查询未使用的
    public Addressext notUsed(Integer coinprotocol) {
        return addressextDao.findFirstByCoinProtocolAndStatus(coinprotocol, 0);
    }

    public Addressext saveAndFlush(Addressext addressext) {
        return addressextDao.saveAndFlush(addressext);
    }

    public Addressext save(Addressext addressext) {
        return addressextDao.save(addressext);
    }

    // 创建
    @Transactional
    public Integer create(Integer id, Integer memberid) {
        return addressextDao.updateMemberIdById(id, memberid);
    }

}
