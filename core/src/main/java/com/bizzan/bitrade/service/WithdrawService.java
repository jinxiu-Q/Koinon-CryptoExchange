package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.constant.WithdrawStatus;
import com.bizzan.bitrade.dao.MemberWalletDao;
import com.bizzan.bitrade.dao.WithdrawDao;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.exception.InformationExpiredException;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class WithdrawService extends BaseService<Withdraw> {

    @Autowired
    private WithdrawDao withdrawDao;
    @Autowired
    private MemberTransactionService transactionService;
    @Autowired
    private MemberWalletDao memberWalletDao;

    public Withdraw findOne(Integer id) {
        return withdrawDao.findFirstById(id);
    }

    public Page<Withdraw> findAll(Predicate predicate, Pageable pageable) {
        return withdrawDao.findAll(predicate, pageable);
    }

    public Iterable<Withdraw> findAllOut(Predicate predicate) {
        return withdrawDao.findAll(predicate, new Sort(Sort.Direction.DESC, "id"));
    }

    public Page<Withdraw> findAllByMemberId(Integer memberId, int page, int pageSize) {
        Sort orders = Criteria.sortStatic("id.desc");
        PageRequest pageRequest = new PageRequest(page, pageSize, orders);
        Criteria<Withdraw> specification = new Criteria<>();
        specification.add(Restrictions.eq("memberid", memberId, false));
        return withdrawDao.findAll(specification, pageRequest);
    }

    @Transactional
    public boolean create(Withdraw withdraw) throws Exception {

        Coin coin = new Coin();
        coin.setName(withdraw.getCoinname());

        // 冻结用户资产
        int ret = memberWalletDao.freezeBalanceByMemberId(withdraw.getMemberid(), coin, BigDecimal.valueOf(withdraw.getMoney()));
        if (ret <= 0) {
            throw new InformationExpiredException("Information Expired");
        }

        withdrawDao.save(withdraw);

        return true;
    }

    @Transactional
    public void save(Withdraw withdraw) {
        // 如果是驳回，则解冻余额
        if (withdraw.getStatus() == -1) {
            Coin coin = new Coin();
            coin.setName(withdraw.getCoinname());
            memberWalletDao.unfreezeBalanceByMemberId(withdraw.getMemberid(), coin, BigDecimal.valueOf(withdraw.getMoney()));
        }
        withdrawDao.update(withdraw.getId(), withdraw.getStatus(), withdraw.getWithdrawinfo(), withdraw.getProcesstime());
    }

    /**
     * 提现成功处理
     *
     * @param coin
     * @param withdraw
     * @param txid
     */
    @Transactional
    public void withdrawSuccess(Withdraw withdraw,Coin coin, String txid) {
        if (withdraw != null) {
            withdraw.setHash(txid);
            withdraw.setStatus(2);
            withdrawDao.updateHash(withdraw.getId(),txid);
            MemberWallet wallet = memberWalletDao.findByCoinAndMemberId(coin, Long.valueOf(withdraw.getMemberid()));
            if (wallet != null) {
                memberWalletDao.decreaseFrozen(wallet.getId(),BigDecimal.valueOf(withdraw.getMoney()));
                MemberTransaction transaction = new MemberTransaction();
                transaction.setAmount(BigDecimal.valueOf(withdraw.getMoney()));
                transaction.setSymbol(wallet.getCoin().getUnit());
                transaction.setAddress(wallet.getAddress());
                transaction.setMemberId(wallet.getMemberId());
                transaction.setType(TransactionType.WITHDRAW);
                transaction.setFee(BigDecimal.valueOf(withdraw.getFee()));
                transaction.setDiscountFee("0");
                transaction.setRealFee(withdraw.getFee()+"");
                transaction.setCreateTime(new Date());
                transaction = transactionService.save(transaction);

            }
        }
    }

    @Transactional
    public void updateWithdrawStatus(Integer id,int status) {
        withdrawDao.updateStatus(id,status);

    }

    public List<Withdraw> findWithrawByStatus(int status) {
        Criteria<Withdraw> specification = new Criteria<>();
        specification.add(Restrictions.eq("status", status, false));
        return withdrawDao.findAll(specification);
    }
}
