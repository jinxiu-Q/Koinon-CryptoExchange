package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.constant.MemberAddressStatus;
import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.MemberAddress;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年01月26日
 */
public interface MemberAddressDao extends BaseDao<MemberAddress> {
    @Modifying
    @Query("update MemberAddress a set a.deleteTime=:date,a.status=1 where a.status=0 and a.id=:id and a.memberId=:memberId")
    int deleteMemberAddress(@Param("date") Date date, @Param("id") long id, @Param("memberId") long memberId);

    @Modifying
    @Query("update MemberAddress a set a.deleteTime=:date,a.status=1 where a.id=:id and a.memberId=:memberId")
    int deleteMemberAddress2(@Param("date") Date date, @Param("id") long id, @Param("memberId") long memberId);

    List<MemberAddress> findAllByMemberIdAndAddressAndStatus(Long memberId, String address, MemberAddressStatus status);

    MemberAddress findByIdAndMemberId(Long id,Long memberId);

    MemberAddress findByConfirmationString(String confirmationString);

    MemberAddress findByAddress(String address);

}
