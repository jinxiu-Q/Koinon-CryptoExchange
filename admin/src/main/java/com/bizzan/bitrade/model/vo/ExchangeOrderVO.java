package com.bizzan.bitrade.model.vo;

import com.bizzan.bitrade.entity.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ExchangeOrderVO {

    private String orderId;
    private Long memberId;
    //挂单类型
    private ExchangeOrderType type;
    //买入或卖出量，对于市价买入单表
    private BigDecimal amount = BigDecimal.ZERO;
    //交易对符号
    private String symbol;
    //成交量
    private BigDecimal tradedAmount = BigDecimal.ZERO;
    //成交额，对市价买单有用
    private BigDecimal turnover = BigDecimal.ZERO;
    //币单位
    private String coinSymbol;
    //结算单位
    private String baseSymbol;
    //订单状态
    private ExchangeOrderStatus status;
    //订单方向
    private ExchangeOrderDirection direction;
    //挂单价格
    private BigDecimal price = BigDecimal.ZERO;
    //挂单时间
    private Long time;
    //交易完成时间
    private Long completedTime;
    //取消时间
    private Long canceledTime;
    //是否使用折扣 0 不使用 1使用
    private  String useDiscount;
    // 已成交手续费
    private  BigDecimal fee;

    private ExchangeOrderResource orderResource = ExchangeOrderResource.CUSTOMER;

    private List<ExchangeOrderDetail> detail;

    private String email;
    private String mobilePhone;
    private String realName;

}
