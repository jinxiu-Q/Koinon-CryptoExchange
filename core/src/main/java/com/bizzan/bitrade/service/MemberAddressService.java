package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.MemberAddressStatus;
import com.bizzan.bitrade.core.Model;
import com.bizzan.bitrade.dao.CoinDao;
import com.bizzan.bitrade.dao.MemberAddressDao;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.MemberAddress;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.bizzan.bitrade.util.Md5;
import com.bizzan.bitrade.util.MessageResult;
import org.apache.tomcat.util.security.MD5Encoder;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年01月26日
 */
@Service
public class MemberAddressService extends BaseService {
    @Autowired
    private MemberAddressDao memberAddressDao;
    @Autowired
    private CoinDao coinDao;

    public MessageResult addMemberAddress(Long memberId, String address, String unit, String remark, Integer protocal,String confirmationString) {
        Coin coin = coinDao.findByUnit(unit);
        if (coin == null || coin.getCanWithdraw().equals(BooleanEnum.IS_FALSE)) {
            return MessageResult.error(600, "The currency does not support withdrawals");
        }
        MemberAddress memberAddress = new MemberAddress();
        memberAddress.setAddress(address);
        memberAddress.setCoin(coin);
        memberAddress.setMemberId(memberId);
        memberAddress.setRemark(remark);
        memberAddress.setProtocol(protocal);
        memberAddress.setConfirmationString(confirmationString);
        MemberAddress memberAddress1 = memberAddressDao.saveAndFlush(memberAddress);
        if (memberAddress1 != null) {
            return MessageResult.success();
        } else {
            return MessageResult.error("failed");
        }
    }

    public MessageResult deleteMemberAddress(Long memberId, Long addressId) {
        int is = memberAddressDao.deleteMemberAddress2(new Date(), addressId, memberId);
        if (is > 0) {
            return MessageResult.success();
        } else {
            return MessageResult.error("failed");
        }
    }

    public MessageResult modifyMemberAddress(Long memberId, Long addressId, String remark) {
        MemberAddress address = memberAddressDao.findByIdAndMemberId(addressId, memberId);
        if (address == null) {
            return MessageResult.error("failed");
        }
        address.setRemark(remark);
        memberAddressDao.saveAndFlush(address);
        return MessageResult.success();
    }

    public MessageResult modifyMemberAddressStatus(MemberAddress address) {
        address.setStatus(MemberAddressStatus.NORMAL);
        memberAddressDao.saveAndFlush(address);
        return MessageResult.success();
    }


    public Page<MemberAddress> pageQuery(int pageNo, Integer pageSize, long id, String unit) {
        Sort orders = Criteria.sortStatic("id.desc");
        PageRequest pageRequest = new PageRequest(pageNo, pageSize, orders);
        Criteria<MemberAddress> specification = new Criteria<>();
        specification.add(Restrictions.eq("memberId", id, false));
        specification.add(Restrictions.eq("status", MemberAddressStatus.NORMAL, false));
        specification.add(Restrictions.eq("coin.unit", unit, false));
        return memberAddressDao.findAll(specification, pageRequest);
    }

    public List<Map<String, String>> queryAddress(long userId, String coinId) {
        try {
            return new Model("member_address")
                    .field(" remark,address,status,protocol,id")
                    .where("member_id=? and coin_id=? and status=?", userId, coinId, MemberAddressStatus.NORMAL.ordinal())
                    .select();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Map<String, String>> queryAddressWaiting(long userId, String coinId) {
        try {
            return new Model("member_address")
                    .field(" remark,address,status,protocol,id")
                    .where("member_id=? and coin_id=? and status=?", userId, coinId, MemberAddressStatus.WAITING.ordinal())
                    .select();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<MemberAddress> findByMemberIdAndAddress(long userId, String address) {
        return memberAddressDao.findAllByMemberIdAndAddressAndStatus(userId, address, MemberAddressStatus.NORMAL);
    }

    public MemberAddress findByConfirmationString(String confirmationString) {
        return memberAddressDao.findByConfirmationString(confirmationString);
    }

    public MemberAddress findByAddress(String address) {
        return memberAddressDao.findByAddress(address);
    }
}
