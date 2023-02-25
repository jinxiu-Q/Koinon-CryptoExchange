package com.bizzan.bitrade.constant;

import com.bizzan.bitrade.core.BaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionType implements BaseEnum {
    RECHARGE("充值"),
    WITHDRAW("提现"),
    TRANSFER_ACCOUNTS("转账"),
    EXCHANGE("币币交易"),
    OTC_BUY("法币买入"),
    OTC_SELL("法币卖出"),
    ACTIVITY_AWARD("活动奖励"),
    PROMOTION_AWARD("推广奖励"),
    DIVIDEND("分红"),
    VOTE("投票"),
    ADMIN_RECHARGE("人工充值"),
    MATCH("配对"),
    ACTIVITY_BUY("活动兑换"),
    CTC_BUY("CTC买入"),
    CTC_SELL("CTC卖出"),
    RED_OUT("红包发出"),
    RED_IN("红包领取"),
    WITHDRAWCODE_OUT("提现码提现"),
    WITHDRAWCODE_IN("提现码充值"),
    CONTRACT_FEE("永续合约手续费"),
    CONTRACT_PROFIT("永续合约盈利"),
    CONTRACT_LOSS("永续合约亏损"),
    OPTION_FAIL("期权合约失败"),
    OPTION_FEE("期权合约手续费"),
    OPTION_REWARD("期权合约奖金"),
    CONTRACT_AWARD("合约返佣"),
    LEVEL_AWARD("平级奖励"),
    PLATFORM_FEE_AWARD("平台手续费收入"),
    SECOND_FAIL("秒合约失败"),
    SECOND_REWARD("秒合约奖金"),
    FINANCE_REWARD("理财利息"),
    PAY_CHARGE_FEE("支出资金费用"),
    GET_CHARGE_FEE("获得资金费用");

    private String cnName;
    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }

    public static TransactionType valueOfOrdinal(int ordinal){
        switch (ordinal){
            case 0:return RECHARGE;
            case 1:return WITHDRAW;
            case 2:return TRANSFER_ACCOUNTS;
            case 3:return EXCHANGE;
            case 4:return OTC_BUY;
            case 5:return OTC_SELL;
            case 6:return ACTIVITY_AWARD;
            case 7:return PROMOTION_AWARD;
            case 8:return DIVIDEND;
            case 9:return VOTE;
            case 10:return ADMIN_RECHARGE;
            case 11:return MATCH;
            case 12:return ACTIVITY_BUY;
            case 13:return CTC_BUY;
            case 14:return CTC_SELL;
            case 15:return RED_OUT;
            case 16:return RED_IN;
            case 17:return WITHDRAWCODE_OUT;
            case 18:return WITHDRAWCODE_IN;
            case 19:return CONTRACT_FEE;
            case 20:return CONTRACT_PROFIT;
            case 21:return CONTRACT_LOSS;
            case 22:return OPTION_FAIL;
            case 23:return OPTION_FEE;
            case 24:return OPTION_REWARD;
            case 25:return CONTRACT_AWARD;
            case 26:return LEVEL_AWARD;
            case 27:return PLATFORM_FEE_AWARD;
            case 28:return SECOND_FAIL;
            case 29:return SECOND_REWARD;
            case 30:return FINANCE_REWARD;
            case 31:return PAY_CHARGE_FEE;
            case 32:return GET_CHARGE_FEE;
            default:return null;
        }
    }
    public static int parseOrdinal(TransactionType ordinal) {
        if (TransactionType.RECHARGE.equals(ordinal)) {
            return 0;
        } else if (TransactionType.WITHDRAW.equals(ordinal)) {
            return 1;
        } else if (TransactionType.TRANSFER_ACCOUNTS.equals(ordinal)) {
            return 2;
        } else if (TransactionType.EXCHANGE.equals(ordinal)) {
            return 3;
        } else if (TransactionType.OTC_BUY.equals(ordinal)) {
            return 4;
        } else if (TransactionType.OTC_SELL.equals(ordinal)) {
            return 5;
        } else if (TransactionType.ACTIVITY_AWARD.equals(ordinal)) {
            return 6;
        }else if (TransactionType.PROMOTION_AWARD.equals(ordinal)) {
            return 7;
        }else if (TransactionType.DIVIDEND.equals(ordinal)) {
            return 8;
        }else if (TransactionType.VOTE.equals(ordinal)) {
            return 9;
        }else if (TransactionType.ADMIN_RECHARGE.equals(ordinal)) {
            return 10;
        }else if (TransactionType.MATCH.equals(ordinal)) {
            return 11;
        }else if (TransactionType.ACTIVITY_BUY.equals(ordinal)) {
            return 12;
        }else if (TransactionType.CTC_BUY.equals(ordinal)) {
            return 13;
        }else if (TransactionType.CTC_SELL.equals(ordinal)) {
            return 14;
        }else if (TransactionType.RED_OUT.equals(ordinal)) {
            return 15;
        }else if (TransactionType.RED_IN.equals(ordinal)) {
            return 16;
        }else if (TransactionType.WITHDRAWCODE_OUT.equals(ordinal)){
            return 17;
        }else if (TransactionType.WITHDRAWCODE_IN.equals(ordinal)){
            return 18;
        }else if(TransactionType.CONTRACT_FEE.equals(ordinal)){
            return 19;
        }else if(TransactionType.CONTRACT_PROFIT.equals(ordinal)){
            return 20;
        }else if(TransactionType.CONTRACT_LOSS.equals(ordinal)){
            return 21;
        }else if(TransactionType.OPTION_FAIL.equals(ordinal)){
            return 22;
        }else if(TransactionType.OPTION_FEE.equals(ordinal)){
            return 23;
        }else if(TransactionType.OPTION_REWARD.equals(ordinal)){
            return 24;
        }else if(TransactionType.CONTRACT_AWARD.equals(ordinal)){
            return 25;
        }else if(TransactionType.LEVEL_AWARD.equals(ordinal)){
            return 26;
        }else if(TransactionType.PLATFORM_FEE_AWARD.equals(ordinal)){
            return 27;
        }else if(TransactionType.SECOND_FAIL.equals(ordinal)){
            return 28;
        }else if(TransactionType.SECOND_REWARD.equals(ordinal)){
            return 29;
        }else if(TransactionType.FINANCE_REWARD.equals(ordinal)) {
            return 30;
        }else if(TransactionType.PAY_CHARGE_FEE.equals(ordinal)){
            return 31;
        }else if(TransactionType.GET_CHARGE_FEE.equals(ordinal)){
            return 32;
        }else {
            return 33;
        }
    }

}
