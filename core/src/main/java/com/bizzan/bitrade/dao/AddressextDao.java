package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.Addressext;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface AddressextDao extends BaseDao<Addressext> {

    Addressext findFirstByMemberIdAndCoinProtocol(Integer memberid, Integer coinprotocol);

    Addressext findFirstByCoinProtocolAndStatus(Integer coinprotocol, Integer status);

    @Modifying
    @Query(value = "update Addressext a set a.memberId = (:memberid),a.status = 1 where a.id = (:id) and a.status = 0")
    Integer updateMemberIdById(@Param("id") Integer id, @Param("memberid") Integer memberid);

    Addressext findFirstByAddressAndStatus(String address, Integer status);
}
