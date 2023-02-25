package com.bizzan.bitrade.controller.system;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.Coinprotocol;
import com.bizzan.bitrade.service.CoinprotocolService;
import com.bizzan.bitrade.util.BindingResultUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 币种协议管理
 */
@Slf4j
@RestController
@RequestMapping("/system/coinprotocol")
public class CoinProtocolController extends BaseAdminController {

    @Autowired
    private CoinprotocolService coinprotocolService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @RequiresPermissions("system:coinprotocol:page-query")
    @PostMapping("/page-query")
    @AccessLog(module = AdminModule.SYSTEM, operation = "获取所有协议列表")
    public MessageResult pageQuery(PageModel pageModel) {

        BooleanExpression predicate = null;

        Page<Coinprotocol> all = coinprotocolService.findAll(predicate, pageModel.getPageable());
        return success(all);
    }

    @RequiresPermissions("system:coinprotocol:merge")
    @PostMapping("/merge")
    @AccessLog(module = AdminModule.SYSTEM, operation = "创建/修改协议")
    public MessageResult merge(@Valid Coinprotocol coinprotocol, BindingResult bindingResult) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }

        // 查询是否存在
        Coinprotocol one = coinprotocolService.findByProtocol(coinprotocol.getProtocol());
        if (coinprotocol.getId() != null) {
            if (one != null && !one.getId().equals(coinprotocol.getId())) {
                result = error("当前协议已存在");
                return result;
            }
        } else if (one != null) {
            result = error("当前协议已存在");
            return result;
        }

        // 删除redis缓存
        redisTemplate.delete("coinprotocol");

        coinprotocol = coinprotocolService.save(coinprotocol);

        result = success("操作成功");
        result.setData(coinprotocol);
        return result;
    }

}
