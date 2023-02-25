package com.bizzan.bitrade.controller;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.core.Convert;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.service.ConvertCoinService;
import com.bizzan.bitrade.service.ConvertOrderService;
import com.bizzan.bitrade.service.MemberTransactionService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.MessageResult;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;

/**
 * 动态兑换币种（前端CExchange）
 */
@RestController
@RequestMapping("convert")
public class CustomExchangeController extends BaseController {

    //@Value("${cexchange.rate.usdt:}")
    private BigDecimal usdtRate;

    //@Value("${cexchange.rate.btc:}")
    private BigDecimal btcRate;

    //@Value("${cexchange.rate.eth:}")
    private BigDecimal ethRate;

    //@Value("${cexchange.rate.nextusdt:}")
    private BigDecimal nextUsdtRate;

    //@Value("${cexchange.rate.nextbtc:}")
    private BigDecimal nextBtcRate;

    //@Value("${cexchange.rate.nexteth:}")
    private BigDecimal nextEthRate;

    //@Value("${cexchange.rate.totalsupply:}")
    private BigDecimal totalSupply;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MemberWalletService walletService;
    @Autowired
    private MemberTransactionService transactionService;
    @Autowired
    private ConvertCoinService coinService;
    @Autowired
    private ConvertOrderService convertOrderService;

    @RequestMapping("get-exchange-rate")
    public MessageResult getRate() {
        JSONObject retObj = new JSONObject();
        retObj.put("usdtRate", usdtRate);
        retObj.put("btcRate", btcRate);
        retObj.put("ethRate", ethRate);
        retObj.put("nextUsdtRate", nextUsdtRate);
        retObj.put("nextBtcRate", nextBtcRate);
        retObj.put("nextEthRate", nextEthRate);
        retObj.put("totalSupply", totalSupply);
        return success(retObj);
    }


    /**
     * 查询所有记录
     *
     * @param member
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping("orderList")
    public MessageResult findTransaction(@SessionAttribute(SESSION_MEMBER) AuthMember member, HttpServletRequest request, int pageNo, int pageSize) throws ParseException {
        MessageResult mr = new MessageResult();
        mr.setCode(0);
        mr.setMessage("success");
        mr.setData(convertOrderService.queryByMember(member.getId(), pageNo, pageSize));
        return mr;
    }

    @RequestMapping("getCoins")
    public MessageResult getCoins() {
        List<ConvertCoin> all = coinService.findByStatus(1);
        return success(all);
    }

    @RequestMapping("getCoinsByUnit")
    public MessageResult getCoinsByUnit(String unit) {
        List<ConvertCoin> all = coinService.findByStatus(1);
        for (int i = all.size()-1; i >=0; i--) {
            ConvertCoin convertCoin = all.get(i);
            if(convertCoin.getCoinUnit().equalsIgnoreCase(unit)){
                all.remove(convertCoin);
            }
        }
        return success(all);
    }

    @RequestMapping("getPrice")
    public MessageResult getPrice(String fromUnit,String toUnit) {
        BigDecimal lastPrice = getLastPrice(fromUnit, toUnit);
        if(lastPrice!=null){
            return success(lastPrice);
        }
        return error("交易对不支持");

    }

    private BigDecimal getLastPrice(String fromUnit,String toUnit){
        String serviceName = "BITRADE-MARKET";
        String url = "http://" + serviceName + "/market/symbol-thumb";
        ParameterizedTypeReference<List<CoinThumb>> typeRef = new ParameterizedTypeReference<List<CoinThumb>>() {};
        ResponseEntity<List<CoinThumb>> responseEntity = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(null), typeRef);
        List<CoinThumb> thumbList =responseEntity.getBody();
        String symbol=fromUnit.toUpperCase()+"/"+toUnit.toUpperCase();
        String symbol2 = toUnit.toUpperCase()+"/"+fromUnit.toUpperCase();
        for (CoinThumb coinThumb : thumbList) {
            if(coinThumb.getSymbol().equalsIgnoreCase(symbol)){
                return coinThumb.getClose();
            }
            if(coinThumb.getSymbol().equalsIgnoreCase(symbol2)){
                return BigDecimal.ONE.divide(coinThumb.getClose(),8, RoundingMode.HALF_UP);
            }
        }
        return null;
    }


    @RequestMapping("do-exchange")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult doExchagne(@SessionAttribute(SESSION_MEMBER) AuthMember member, String fromUnit,String toUnit, BigDecimal needAmount) {

        ConvertCoin convertCoin = coinService.findByCoinUnit(toUnit);
        //获取现价
        BigDecimal lastPrice = getLastPrice(fromUnit, toUnit);
        if(lastPrice==null){
            return error("交易对不支持");
        }
        // 检查余额是否足够
        MemberWallet needMW = walletService.findByCoinUnitAndMemberId(fromUnit, member.getId());
        if(needMW.getBalance().compareTo(needAmount) < 0) {
            return error("Balance is not enough");
        }
        // 执行兑换
        MemberWallet targetMW = walletService.findByCoinUnitAndMemberId(toUnit, member.getId());
        BigDecimal amount = needAmount.multiply(lastPrice);

        BigDecimal fee = amount.multiply(convertCoin.getFee());
        amount = amount.subtract(fee);
        //创建交易
        ConvertOrder order = new ConvertOrder();
        order.setFee(fee);
        order.setCreateTime(new Date());
        order.setMemberId(member.getId());
        order.setFromUnit(fromUnit);
        order.setToUnit(toUnit);
        order.setFromAmount(needAmount);
        order.setToAmount(amount);
        order.setPrice(lastPrice);
        order.setStatus(1);
        convertOrderService.save(order);

        // 增加目标余额
        walletService.increaseBalance(targetMW.getId(), amount);

        // 减少基币余额
        walletService.decreaseBalance(needMW.getId(), needAmount);

        // 增加资金变更记录（增加）
        MemberTransaction memberTransaction = new MemberTransaction();
        memberTransaction.setFee(BigDecimal.ZERO);
        memberTransaction.setAmount(amount);
        memberTransaction.setMemberId(member.getId());
        memberTransaction.setSymbol(toUnit);
        memberTransaction.setType(TransactionType.ACTIVITY_BUY);
        memberTransaction.setCreateTime(DateUtil.getCurrentDate());
        memberTransaction.setRealFee("0");
        memberTransaction.setDiscountFee("0");
        memberTransaction= transactionService.save(memberTransaction);

        // 增加资金变更记录（减少记录）
        MemberTransaction memberTransactionOut = new MemberTransaction();
        memberTransactionOut.setFee(BigDecimal.ZERO);
        memberTransactionOut.setAmount(needAmount);
        memberTransactionOut.setMemberId(member.getId());
        memberTransactionOut.setSymbol(fromUnit);
        memberTransactionOut.setType(TransactionType.ACTIVITY_BUY);
        memberTransactionOut.setCreateTime(DateUtil.getCurrentDate());
        memberTransactionOut.setRealFee("0");
        memberTransactionOut.setDiscountFee("0");
        memberTransactionOut= transactionService.save(memberTransactionOut);

        return success("Exchange successful");
    }
}
