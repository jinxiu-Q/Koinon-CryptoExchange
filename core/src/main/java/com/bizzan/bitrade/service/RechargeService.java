package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.MemberWalletDao;
import com.bizzan.bitrade.dao.RechargeDao;
import com.bizzan.bitrade.dao.RedEnvelopeDetailDao;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.entity.Recharge;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
public class RechargeService extends BaseService<Recharge> {

    @Autowired
    private RechargeDao rechargeDao;

    @Autowired
    private MemberWalletDao memberWalletDao;

    @Autowired
    private CoinService coinService;

    public Page<Recharge> findAllByMemberId(Integer memberId, int page, int pageSize) {
        Sort orders = Criteria.sortStatic("id.desc");
        PageRequest pageRequest = new PageRequest(page, pageSize, orders);
        Criteria<Recharge> specification = new Criteria<>();
        specification.add(Restrictions.eq("memberid", memberId, false));
        return rechargeDao.findAll(specification, pageRequest);
    }


    public Page<Recharge> findAll(Predicate predicate, Pageable pageable) {
        return rechargeDao.findAll(predicate, pageable);
    }

    public Recharge save(Recharge recharge) {
        return rechargeDao.save(recharge);
    }


    public Iterable<Recharge> findAllOut(Predicate predicate) {
        return rechargeDao.findAll(predicate, new Sort(Sort.Direction.DESC, "id"));
    }


    public List<MemberWallet> findAllByMemberId(Long memberId) {
        List<MemberWallet> list = memberWalletDao.findAllByMemberId(memberId);
        //补全钱包
        List<Coin> coins = coinService.findAll();
        for (Coin coin : coins) {
            boolean isHas = false;
            for (MemberWallet memberWallet : list) {
                if (memberWallet.getCoin().getUnit().equals(coin.getUnit())) {
                    isHas = true;
                }
            }
            if (!isHas) {
                MemberWallet wallet = new MemberWallet();
                wallet.setCoin(coin);
                wallet.setMemberId(memberId);
                wallet.setBalance(new BigDecimal(0));
                wallet.setFrozenBalance(new BigDecimal(0));
                wallet.setAddress("");
                this.save(wallet);
                list.add(wallet);
            }
        }
        //删除不能充值的
        List<MemberWallet> listResult = new ArrayList<>();
        for (MemberWallet memberWallet : list) {
            if (memberWallet.getCoin().getCanRecharge().isIs()) {
                listResult.add(memberWallet);
            }
        }
        return listResult;
    }

}
