package com.bizzan.bitrade.controller.finance;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.dto.CoinDTO;
import com.bizzan.bitrade.dto.CoinprotocolDTO;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.QRecharge;
import com.bizzan.bitrade.entity.Recharge;
import com.bizzan.bitrade.model.screen.RechargeScreen;
import com.bizzan.bitrade.model.vo.RechargeExcelVO;
import com.bizzan.bitrade.model.vo.RechargeVO;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.CoinprotocolService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.RechargeService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 充值管理
 */
@Slf4j
@RestController
@RequestMapping("/finance/recharge")
public class RechargeController extends BaseAdminController {

    @Autowired
    private CoinService coinService;

    @Autowired
    private CoinprotocolService coinprotocolService;

    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private MemberService memberService;

    @RequiresPermissions("finance:recharge:coin-list")
    @GetMapping("/coin-list")
    @AccessLog(module = AdminModule.FINANCE, operation = "充值记录里获取币种列表")
    public MessageResult coinList() {

        List<CoinDTO> list = coinService.list();

        return success(list);
    }

    @RequiresPermissions("finance:recharge:protocol-list")
    @GetMapping("/protocol-list")
    @AccessLog(module = AdminModule.FINANCE, operation = "充值记录里获取币种协议列表")
    public MessageResult protocolList() {

        List<CoinprotocolDTO> list = coinprotocolService.list();

        return success(list);
    }

    @RequiresPermissions("finance:recharge:page-query")
    @PostMapping("/page-query")
    @AccessLog(module = AdminModule.FINANCE, operation = "获取充值记录")
    public MessageResult pageQuery(PageModel pageModel, RechargeScreen rechargeScreen,
                                   HttpServletResponse response) throws IOException {

        List<BooleanExpression> booleanExpressions = new ArrayList<>();

        String address = rechargeScreen.getAddress();
        if (!StringUtils.isBlank(address)) {
            booleanExpressions.add(QRecharge.recharge.address.eq(address));
        }

        Integer protocol = rechargeScreen.getProtocol();
        if (protocol != null && protocol > 0) {
            booleanExpressions.add(QRecharge.recharge.protocol.eq(protocol));
        }

        String coinname = rechargeScreen.getCoinname();
        if (!StringUtils.isBlank(coinname)) {
            booleanExpressions.add(QRecharge.recharge.coinname.eq(coinname));
        }

        Predicate predicate = PredicateUtils.getPredicate(booleanExpressions);

        // 导出
        if (rechargeScreen.getIsOut() == 1) {
            Iterable<Recharge> allOut = rechargeService.findAllOut(predicate);
            Set<Long> memberSet = new HashSet<>();
            allOut.forEach(v -> {
                memberSet.add(Long.valueOf(v.getMemberid()));
            });
            Map<Long, Member> memberMap = memberService.mapByMemberIds(new ArrayList<>(memberSet));

            List<RechargeExcelVO> voList = new ArrayList<>();

            allOut.forEach(v -> {
                RechargeExcelVO vo = new RechargeExcelVO();
                BeanUtils.copyProperties(v, vo);

                vo.setMemberId(Long.valueOf(v.getMemberid()));

                vo.setMoney(String.valueOf(v.getMoney()));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (v.getAddtime() != null && v.getAddtime() > 0) {
                    vo.setAddtime(sdf.format(new Date(v.getAddtime())));
                } else {
                    vo.setAddtime("--");
                }

                Integer status = v.getStatus();
                String statusStr = "";
                if (status == 0) {
                    statusStr = "未到账";
                } else if (status == 1) {
                    statusStr = "已到账";
                } else {
                    statusStr = "失败";
                }
                vo.setStatus(statusStr);

                vo.setConfirms(v.getConfirms() + "/" + v.getNconfirms());

                Long memberId = vo.getMemberId();
                if (memberMap.containsKey(memberId)) {
                    Member member = memberMap.get(memberId);
                    vo.setEmail(member.getEmail());
                    vo.setMobilePhone(member.getMobilePhone());
                }
                voList.add(vo);

            });

            ExcelUtil.listToExcel(voList, RechargeExcelVO.class.getDeclaredFields(), response.getOutputStream());

            return null;
        }


        Page<Recharge> all = rechargeService.findAll(predicate, pageModel.getPageable());

        List<Long> memberIds = all.getContent().stream().map(v -> (long) v.getMemberid()).collect(Collectors.toList());
        Map<Long, Member> memberMap = memberService.mapByMemberIds(memberIds);

        Page<RechargeVO> page = all.map(v -> {
            RechargeVO rechargeVO = new RechargeVO();
            BeanUtils.copyProperties(v, rechargeVO);
            Long memberid = (long) rechargeVO.getMemberid();
            if (memberMap.containsKey(memberid)) {
                rechargeVO.setUsername(memberMap.get(memberid).getUsername());
            }
            return rechargeVO;
        });

        return success(page);
    }

}
