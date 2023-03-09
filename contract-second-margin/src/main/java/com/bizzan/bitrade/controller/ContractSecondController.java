package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.processor.ContractProcessor;
import com.bizzan.bitrade.processor.ContractProcessorFactory;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.util.MessageResult;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;

import java.math.BigDecimal;

/**
 * 委托订单处理类
 */
@Slf4j
@RestController
@RequestMapping("/second")
public class ContractSecondController {
    @Autowired
    private ExchangeOrderCircleService ordercricleService;
    @Autowired
    private MemberWalletService walletService;
    @Autowired
    private ExchangeCoinService exchangeCoinService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private ContractProcessorFactory contractProcessorFactory;
    @Autowired
    private LocaleMessageSourceService msService;
    @Autowired
    private MemberService memberService;

    /**
     * 添加秒合约订单
     *
     * @param authMember
     * @param direction
     * @param symbol
     * @param amount
     * @return
     */
    @RequestMapping("add")
    public MessageResult addOrder(@SessionAttribute(SESSION_MEMBER) AuthMember authMember,
                                  String symbol, ExchangeOrderDirection direction, ExchangeOrderCirclePeriod period, ExchangeOrderCircleOdds odds,
                                  Integer amount) {

        if (direction == null) {
            return MessageResult.error(500, msService.getMessage("ILLEGAL_ARGUMENT"));
        }
        Member member = memberService.findOne(authMember.getId());
        //是否被禁止交易
        if (member.getTransactionStatus().equals(BooleanEnum.IS_FALSE)) {
            return MessageResult.error(500, msService.getMessage("CANNOT_TRADE"));
        }

        //判断数量小于零
        if (amount <= 0) {
            return MessageResult.error(500, msService.getMessage("NUMBER_OF_ILLEGAL"));
        }
        //根据交易对名称（symbol）获取交易对儿信息
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        if (exchangeCoin == null) {
            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
        }
        if (exchangeCoin.getEnable() != 1 || exchangeCoin.getCircle() != 1) {
            return MessageResult.error(500, msService.getMessage("COIN_FORBIDDEN"));
        }
        //获取基准币
        String baseCoin = exchangeCoin.getBaseSymbol();
        String exCoin = exchangeCoin.getCoinSymbol();
        Coin coin = coinService.findByUnit(baseCoin);

        if (coin == null) {
            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
        }
        //数量范围控制
        if (exchangeCoin.getMaxAmountCircle() != null && exchangeCoin.getMaxAmountCircle() != 0
                && exchangeCoin.getMaxAmountCircle().compareTo(amount) < 0) {
            return MessageResult.error(msService.getMessage("AMOUNT_OVER_SIZE") + " " + exchangeCoin.getMaxAmountCircle());
        }
        if (exchangeCoin.getMinAmountCircle() != null && exchangeCoin.getMinAmountCircle() != 0
                && exchangeCoin.getMinAmountCircle().compareTo(amount) > 0) {
            return MessageResult.error(msService.getMessage("AMOUNT_TOO_SMALL") + " " + exchangeCoin.getMinAmountCircle());
        }
        MemberWallet baseCoinWallet = walletService.findByCoinUnitAndMemberId(baseCoin, member.getId());
        if (baseCoinWallet == null) {
            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
        }
        if (baseCoinWallet.getIsLock() == BooleanEnum.IS_TRUE) {
            return MessageResult.error(500, msService.getMessage("WALLET_LOCKED"));
        }

        ExchangeOrderCricle order = new ExchangeOrderCricle();
        order.setMemberId(member.getId());
        order.setSymbol(symbol);
        order.setBaseSymbol(baseCoin);
        order.setCoinSymbol(exCoin);
        order.setPeriod(period);
        order.setType(ExchangeOrderType.MARKET_PRICE);
        order.setDirection(direction);

        ContractProcessor processor = contractProcessorFactory.getProcessor(symbol);
        order.setPrice(processor.getCurPrice());
        order.setUseOdds(odds);
        //限价买入单时amount为用户设置的总成交额
        order.setAmount(new BigDecimal(amount));
        order.setUsdtValue(amount.doubleValue());

        MessageResult mr = ordercricleService.addOrder(member.getId(), order);
        if (mr.getCode() != 0) {
            return MessageResult.error(500, msService.getMessage("ORDER_FAILED") + mr.getMessage());
        }
        log.info(">>>>>>>>>>订单提交完成>>>>>>>>>>");
        //将订单压入处理系统中
        processor.addExchangeOrderCircle(order);
        //
        MessageResult result = MessageResult.success(msService.getMessage("EXAPI_SUCCESS"));
        result.setData(order);
        return result;
    }


    /**
     * 个人委托
     */
    @RequestMapping("personal/history")
    public Page<ExchangeOrderCricle> personalHistoryOrder(@SessionAttribute(SESSION_MEMBER) AuthMember member,
                                                          @RequestParam(value = "symbol", required = false) String symbol,
                                                          @RequestParam(value = "status", required = false) ExchangeOrderStatus status,
                                                          @RequestParam(value = "startTime", required = false) String startTime,
                                                          @RequestParam(value = "endTime", required = false) String endTime,
                                                          @RequestParam(value = "direction", required = false) ExchangeOrderDirection direction,
                                                          @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                                                          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        Page<ExchangeOrderCricle> page = ordercricleService.findPersonalHistory(member.getId(), symbol, status, startTime, endTime, direction, pageNo, pageSize);
        return page;
    }


    @RequestMapping("win")
    public Page<ExchangeOrderCricle> winHistoryOrder( @RequestParam(value = "symbol", required = false) String symbol,@RequestParam(value = "pageNo", defaultValue = "1") int pageNo, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Page<ExchangeOrderCricle> page = ordercricleService.findWinHistory(symbol,pageNo, pageSize);
        return page;
    }

    /**
     * 取消委托
     *
     * @param member
     * @param orderId
     * @return
     */
    @RequestMapping("cancel/{orderId}")
    public MessageResult cancelOrder(@SessionAttribute(SESSION_MEMBER) AuthMember member, @PathVariable String orderId) {
        ExchangeOrderCricle order = ordercricleService.findOne(orderId);
        if (order.getMemberId() != member.getId()) {
            return MessageResult.error(500, msService.getMessage("OPERATION_FORBIDDEN"));
        }
        if (order.getStatus() != ExchangeOrderStatus.TRADING) {
            return MessageResult.error(500, msService.getMessage("ORDER_STATUS_ERROR"));
        }
        //先取消数据库中的
        ordercricleService.cancelOrder(orderId);
        //再取消交易中的
        ContractProcessor processor = contractProcessorFactory.getProcessor(order.getSymbol());
        processor.cancelExchangeOrderCircle(order);
        return MessageResult.success(msService.getMessage("EXAPI_SUCCESS"));
    }

}
