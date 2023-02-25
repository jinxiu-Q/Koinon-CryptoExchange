package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.dto.CoinextDTO;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.system.CoinExchangeFactory;
import com.bizzan.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;

/**
 * @author Hevin E-Mali:390330302@qq.com
 * @date 2021年01月26日
 */
@RestController
@Slf4j
@RequestMapping(value = "/recharge", method = RequestMethod.POST)
public class RechargeController {

    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private CoinService coinService;

    @Autowired
    private CoinextService coinextService;
    @Autowired
    private AddressextService addressextService;
    @Autowired
    private CoinExchangeFactory coinExchangeFactory;

    /**
     * 充值记录
     */
    @RequestMapping("list")
    public MessageResult withdrawCode(@SessionAttribute(SESSION_MEMBER) AuthMember user, int page, int pageSize) {

        MessageResult mr = new MessageResult(0, "success");

        Page<Recharge> records = rechargeService.findAllByMemberId((int) user.getId(), page, pageSize);
        mr.setData(records);
        return mr;
    }

    /**
     * 会员充值前，选择界面
     */

    @RequestMapping("support/list")
    public MessageResult supportWithdrawCode(@SessionAttribute(SESSION_MEMBER) AuthMember user) {
        MessageResult mr = new MessageResult(0, "success");
        List<MemberWallet> records = rechargeService.findAllByMemberId(user.getId());
        for (MemberWallet wallet : records) {
            CoinExchangeFactory.ExchangeRate rate = coinExchangeFactory.get(wallet.getCoin().getUnit());
            if (rate != null) {
                wallet.getCoin().setUsdRate(rate.getUsdRate().doubleValue());
                wallet.getCoin().setCnyRate(rate.getCnyRate().doubleValue());
                wallet.getCoin().setJpyRate(rate.getJpyRate().doubleValue());
                wallet.getCoin().setEurRate(rate.getEurRate().doubleValue());
                wallet.getCoin().setHkdRate(rate.getHkdRate().doubleValue());
                wallet.getCoin().setGbpRate(rate.getGbpRate().doubleValue());
                wallet.getCoin().setAudRate(rate.getAudRate().doubleValue());
                wallet.getCoin().setCadRate(rate.getCadRate().doubleValue());
                wallet.getCoin().setAedRate(rate.getAedRate().doubleValue());
            } else {
                log.info("unit = {} , rate = null ", wallet.getCoin().getUnit());
            }
        }
        mr.setData(records);
        return mr;
    }


    @RequestMapping("support/unit")
    public MessageResult supportWithdrawCode(@SessionAttribute(SESSION_MEMBER) AuthMember user, String unit) {
        MessageResult mr = new MessageResult(0, "success");
        Coin coin = coinService.findByUnit(unit);
        List<CoinextDTO> list = coinextService.findByCoinname(coin.getName());
        List<Addressext> addList = new ArrayList<>();
        for (CoinextDTO ex : list) {
            Addressext address = addressextService.read((int) user.getId(), ex.getProtocol());
            addList.add(address);
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("method", list);
        map.put("address", addList);
        mr.setData(map);
        return mr;
    }

}
