package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.dto.CoinprotocolDTO;
import com.bizzan.bitrade.entity.Coinprotocol;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CoinprotocolDao extends BaseDao<Coinprotocol> {


    @Query("select new com.bizzan.bitrade.dto.CoinprotocolDTO(a.protocol,a.protocolname) from Coinprotocol a")
    List<CoinprotocolDTO> list();

    Coinprotocol findFirstByProtocol(Integer protocol);

}
