package com.bizzan.bitrade.job;


import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.processor.ContractProcessor;
import com.bizzan.bitrade.processor.ContractProcessorFactory;
import com.bizzan.bitrade.service.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Component
public class cantractSecondOrderUpdateJob {

    @Autowired
    private ContractProcessorFactory contractProcessorFactory;

    @Autowired
    private ExchangeCoinService service;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ExchangeOrderCircleService ordercricleService;


    //
//
    @Scheduled(cron = "* * * * * *")
    public void autoProcessorHandle() {
        long time = System.currentTimeMillis();
        time = time / 1000;
        time = time * 1000;
        long finalTime = time;
        contractProcessorFactory.getProcessorMap().forEach((symbol, processor) -> {
            processor.handle(finalTime);
        });
    }

    @Scheduled(cron = "* * * * * *")
    public void autoOrder() {
        List<ExchangeCoin> baseSymbol = service.getContractSecondSymbol();
        if (baseSymbol != null && baseSymbol.size() > 0) {

            for (int i = 0; i < baseSymbol.size(); i++) {
                ExchangeCoin exchangeCoin = baseSymbol.get(i);
                ExchangeOrderDirection direction = getRandomNumber(0, 1) == 0 ? ExchangeOrderDirection.BUY : ExchangeOrderDirection.SELL;
                Member member = memberService.findOne((long) 1);
                Integer amount = getRandomNumber(exchangeCoin.getMinAmountCircle(), exchangeCoin.getMaxAmountCircle());

                Integer period = getRandomNumber(0, 3);
                Integer odds = getRandomNumber(0, 3);
                //获取基准币
                String baseCoin = exchangeCoin.getBaseSymbol();
                String exCoin = exchangeCoin.getCoinSymbol();

                ExchangeOrderCricle order = new ExchangeOrderCricle();
                order.setMemberId(member.getId());
                order.setSymbol(exchangeCoin.getSymbol());
                order.setBaseSymbol(baseCoin);
                order.setCoinSymbol(exCoin);
                order.setPeriod(ExchangeOrderCirclePeriod.values()[period]);
                order.setType(ExchangeOrderType.MARKET_PRICE);
                order.setDirection(direction);

                ContractProcessor processor = contractProcessorFactory.getProcessor(exchangeCoin.getSymbol());
                order.setPrice(processor.getCurPrice());
                order.setUseOdds(ExchangeOrderCircleOdds.values()[odds]);
                //限价买入单时amount为用户设置的总成交额
                order.setAmount(new BigDecimal(amount));
                order.setUsdtValue(amount.doubleValue());

                ordercricleService.addOrder(member.getId(), order);
                processor.addExchangeOrderCircle(order);
            }
        }


    }

    public static int getRandomNumber(int from, int to) {
        float a = from + (to - from) * (new Random().nextFloat());
        int b = (int) a;
        return ((a - b) > 0.5 ? 1 : 0) + b;
    }
}
