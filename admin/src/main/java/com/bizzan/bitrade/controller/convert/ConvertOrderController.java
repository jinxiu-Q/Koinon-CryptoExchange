package com.bizzan.bitrade.controller.convert;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.*;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.model.screen.ConvertOrderScreen;
import com.bizzan.bitrade.model.vo.WithdrawExcelVO;
import com.bizzan.bitrade.model.vo.WithdrawVO;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.util.ExcelUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.PredicateUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/convert/order")
public class ConvertOrderController extends BaseAdminController {
    @Autowired
    private ConvertOrderService convertOrderService;


    @RequiresPermissions(value = {"convert:order:page-query"})
    @RequestMapping("/page-query")
    @AccessLog(module = AdminModule.FINANCE, operation = "闪兑订单列表")
    public MessageResult pageQuery(
            PageModel pageModel,
            ConvertOrderScreen screen) {

        if (pageModel.getDirection() == null && pageModel.getProperty() == null) {
            ArrayList<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setDirection(directions);
            List<String> property = new ArrayList<>();
            property.add("createTime");
            pageModel.setProperty(property);
        }
        List<BooleanExpression> booleanExpressions = new ArrayList<>();

        if (screen.getMemberId() != null) {
            booleanExpressions.add(QConvertOrder.convertOrder.memberId.eq(screen.getMemberId()));
        }
        String fromUnit = screen.getFromUnit();
        if (!StringUtils.isBlank(fromUnit)) {
            booleanExpressions.add(QConvertOrder.convertOrder.fromUnit.eq(fromUnit));
        }
        String toUnit = screen.getToUnit();
        if (!StringUtils.isBlank(toUnit)) {
            booleanExpressions.add(QConvertOrder.convertOrder.toUnit.eq(toUnit));
        }
        if(screen.getStartTime() != null) {
            booleanExpressions.add(QConvertOrder.convertOrder.createTime.goe(screen.getStartTime()));
        }
        if(screen.getEndTime() != null) {
            booleanExpressions.add(QConvertOrder.convertOrder.createTime.loe(screen.getEndTime()));
        }
        Predicate predicate = PredicateUtils.getPredicate(booleanExpressions);
        Page<ConvertOrder> pageListMapResult = convertOrderService.findAll(predicate, pageModel.getPageable());
        return success(pageListMapResult);
    }
}
