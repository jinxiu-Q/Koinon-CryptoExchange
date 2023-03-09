package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.entity.ExchangeCoin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bizzan.bitrade.service.ExchangeCoinService;
import com.bizzan.bitrade.util.MessageResult;

import java.util.List;

/**
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/1816:54
 */
@RestController
@RequestMapping("second-symbol")
public class ContractSecondInfoController extends BaseController {
    @Autowired
    private ExchangeCoinService service;

    //获取基币
    @RequestMapping("symbol")
    public MessageResult baseSymbol() {
        List<ExchangeCoin> baseSymbol = service.getContractSecondSymbol();
        if (baseSymbol != null && baseSymbol.size() > 0) {
            return success(baseSymbol);
        }
        return error("Symbol null");
    }

}
