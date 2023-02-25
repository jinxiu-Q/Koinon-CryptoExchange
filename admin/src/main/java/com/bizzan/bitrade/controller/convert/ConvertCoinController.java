package com.bizzan.bitrade.controller.convert;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.Admin;
import com.bizzan.bitrade.entity.ConvertCoin;
import com.bizzan.bitrade.service.ConvertCoinService;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.util.BindingResultUtil;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.Assert.notNull;

/**
 * @author Hevin E-Mali:390330302@qq.com
 * @description 后台货币web
 * @date 2021/12/29 15:01
 */
@RestController
@RequestMapping("/convert/coin")
@Slf4j
public class ConvertCoinController extends BaseAdminController {


    @Autowired
    private ConvertCoinService convertCoinService;

    @Autowired
    private LocaleMessageSourceService messageSource;


    @RequiresPermissions("convert:coin:create")
    @PostMapping("create")
    @AccessLog(module = AdminModule.SYSTEM, operation = "创建闪兑货币Coin")
    public MessageResult create(@Valid ConvertCoin convertCoin) {
        ConvertCoin one = convertCoinService.findByCoinUnit(convertCoin.getCoinUnit());
        if (one != null) {
            return error(messageSource.getMessage("COIN_NAME_EXIST"));
        }
        convertCoin.setUpdateTime(DateUtil.getCurrentDate());
        convertCoin.setCreateTime(DateUtil.getCurrentDate());
        convertCoinService.save(convertCoin);

        return success();
    }


    @RequiresPermissions("convert:coin:update")
    @PostMapping("update")
    @AccessLog(module = AdminModule.SYSTEM, operation = "更新闪兑货币Coin")
    public MessageResult update(
            @Valid ConvertCoin convertCoin,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin,
            BindingResult bindingResult) {

        Assert.notNull(admin, messageSource.getMessage("DATA_EXPIRED_LOGIN_AGAIN"));

        notNull(convertCoin.getCoinUnit(), "validate Coin.Unit!");
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        ConvertCoin one = convertCoinService.findByCoinUnit(convertCoin.getCoinUnit());
        notNull(one, "validate coin.name!");
        convertCoin.setUpdateTime(DateUtil.getCurrentDate());
        convertCoinService.save(convertCoin);
        return success();
    }

    @RequiresPermissions("convert:coin:detail")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.SYSTEM, operation = "后台闪兑Coin详情")
    public MessageResult detail(@RequestParam("coinUnit") String coinUnit) {
        ConvertCoin convertCoin = convertCoinService.findByCoinUnit(coinUnit);
        notNull(convertCoin, "validate Coin.Unit!");
        return success(convertCoin);
    }

    @RequiresPermissions("convert:coin:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.SYSTEM, operation = "分页查找闪兑货币Coin")
    public MessageResult pageQuery(PageModel pageModel) {
        if (pageModel.getProperty() == null) {
            List<String> list = new ArrayList<>();
            list.add("createTime");
            List<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setProperty(list);
            pageModel.setDirection(directions);
        }
        Page<ConvertCoin> pageResult = convertCoinService.findAll(null, pageModel.getPageable());
        return success(pageResult);
    }

//    @PostMapping("all-coin-unit")
//    @AccessLog(module = AdminModule.SYSTEM, operation = "查找convertcoin的unit")
//    public MessageResult getAllCoinUnit() {
//        List<String> list = convertCoinService.getAllCoinUnit();
//        return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"), list);
//    }

}
