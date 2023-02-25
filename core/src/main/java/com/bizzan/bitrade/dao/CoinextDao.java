package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.dto.CoinextDTO;
import com.bizzan.bitrade.entity.Coinext;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CoinextDao extends BaseDao<Coinext> {

    Coinext findFirstByCoinnameAndProtocol(String coinName, Integer protocol);

    List<CoinextDTO>  findByCoinname(String coinName);

    @Query("select new com.bizzan.bitrade.dto.CoinextDTO(coinname,protocol,protocolname,decimals,withdrawfee,minwithdrawfee,iswithdraw,isrecharge,isautowithdraw,minwithdraw,maxwithdraw,minrecharge,confirms,memoaddress) from Coinext where status = 1")
    List<CoinextDTO> list();
}
