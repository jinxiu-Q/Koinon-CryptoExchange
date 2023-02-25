package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.Withdraw;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface WithdrawDao extends BaseDao<Withdraw> {


    @Modifying
    @Query("update Withdraw a set a.status=:status, a.withdrawinfo=:withdrawinfo, a.processtime=:processtime where a.id=:id and (a.status=0 or a.status=3)")
    void update(@Param("id") Integer id, @Param("status") Integer status, @Param("withdrawinfo") String withdrawinfo, @Param("processtime") Long processtime);

    Withdraw findFirstById(Integer id);

    @Modifying
    @Query("update Withdraw a set a.status=:status where a.id=:id ")
    void updateStatus(@Param("id")Integer id, @Param("status") Integer status);

    @Modifying
    @Query("update Withdraw a set a.hash=:hash where a.id=:id ")
    void updateHash(@Param("id")Integer id, @Param("hash") String hash);
}
