package com.bizzan.bitrade.controller.finance;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.dto.CoinDTO;
import com.bizzan.bitrade.dto.CoinprotocolDTO;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.QWithdraw;
import com.bizzan.bitrade.entity.Withdraw;
import com.bizzan.bitrade.model.screen.WithdrawScreen;
import com.bizzan.bitrade.model.vo.WithdrawExcelVO;
import com.bizzan.bitrade.model.vo.WithdrawVO;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.CoinprotocolService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.WithdrawService;
import com.bizzan.bitrade.util.BindingResultUtil;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 提币管理
 */
@Slf4j
@RestController
@RequestMapping("/finance/withdraw")
public class WithDrawController extends BaseAdminController {

    @Autowired
    private CoinService coinService;

    @Autowired
    private CoinprotocolService coinprotocolService;

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    private MemberService memberService;

    @RequiresPermissions("finance:withdraw:coin-list")
    @GetMapping("/coin-list")
    @AccessLog(module = AdminModule.FINANCE, operation = "提币审核里获取币种列表")
    public MessageResult coinList() {

        List<CoinDTO> list = coinService.list();

        return success(list);
    }

    @RequiresPermissions("finance:withdraw:protocol-list")
    @GetMapping("/protocol-list")
    @AccessLog(module = AdminModule.FINANCE, operation = "提币审核里获取币种协议列表")
    public MessageResult protocolList() {

        List<CoinprotocolDTO> list = coinprotocolService.list();

        return success(list);
    }

    @RequiresPermissions("finance:withdraw:page-query")
    @PostMapping("/page-query")
    @AccessLog(module = AdminModule.FINANCE, operation = "获取提币审核列表")
    public MessageResult pageQuery(PageModel pageModel, WithdrawScreen withdrawScreen,
                                   HttpServletResponse response) throws IOException {

        List<BooleanExpression> booleanExpressions = new ArrayList<>();

        String email = withdrawScreen.getEmail();
        if (!StringUtils.isBlank(email)) {
            Member byEmail = memberService.findByEmail(email);
            if (byEmail != null) {
                booleanExpressions.add(QWithdraw.withdraw.memberid.eq(byEmail.getId().intValue()));
            }
        }
        String tel = withdrawScreen.getTel();
        if (!StringUtils.isBlank(tel)) {
            Member byPhone = memberService.findByPhone(tel);
            if (byPhone != null) {
                booleanExpressions.add(QWithdraw.withdraw.memberid.eq(byPhone.getId().intValue()));
            }
        }


        String address = withdrawScreen.getAddress();
        if (!StringUtils.isBlank(address)) {
            booleanExpressions.add(QWithdraw.withdraw.address.eq(address));
        }

        Integer protocol = withdrawScreen.getProtocol();
        if (protocol != null && protocol > 0) {
            booleanExpressions.add(QWithdraw.withdraw.protocol.eq(protocol));
        }

        String coinname = withdrawScreen.getCoinname();
        if (!StringUtils.isBlank(coinname)) {
            booleanExpressions.add(QWithdraw.withdraw.coinname.eq(coinname));
        }

        Integer status = withdrawScreen.getStatus();
        if (status != null && status > -2) {
            booleanExpressions.add(QWithdraw.withdraw.status.eq(status));
        }

        String hash = withdrawScreen.getHash();
        if (!StringUtils.isBlank(hash)) {
            booleanExpressions.add(QWithdraw.withdraw.hash.eq(hash));
        }

        String startAddTime = withdrawScreen.getStartAddTime();
        String endAddTime = withdrawScreen.getEndAddTime();
        if (!StringUtils.isBlank(startAddTime) && !StringUtils.isBlank(endAddTime)) {
            Date startAddTimeDate = null;
            Date endAddTimeDate = null;
            try {
                startAddTimeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startAddTime);
                endAddTimeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endAddTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (startAddTimeDate != null && endAddTimeDate != null) {
                booleanExpressions.add(QWithdraw.withdraw.addtime.between(startAddTimeDate.getTime(),endAddTimeDate.getTime()));
            }
        }

        String startProcessTime = withdrawScreen.getStartProcessTime();
        String endProcessTime = withdrawScreen.getEndProcessTime();
        if (!StringUtils.isBlank(startProcessTime) && !StringUtils.isBlank(endProcessTime)) {
            Date startProcessTimeDate = null;
            Date endProcessTimeDate = null;
            try {
                startProcessTimeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startProcessTime);
                endProcessTimeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endProcessTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (startProcessTimeDate != null && endProcessTimeDate != null) {
                booleanExpressions.add(QWithdraw.withdraw.processtime.between(startProcessTimeDate.getTime(),endProcessTimeDate.getTime()));
            }
        }

        // log.info("条件：{}", booleanExpressions);

        Predicate predicate = PredicateUtils.getPredicate(booleanExpressions);


        // 导出
        if (withdrawScreen.getIsOut() == 1) {

            Iterable<Withdraw> allOut = withdrawService.findAllOut(predicate);
            Set<Long> memberSet = new HashSet<>();
            allOut.forEach(v -> {
                memberSet.add(Long.valueOf(v.getMemberid()));
            });
            Map<Long, Member> memberMap = memberService.mapByMemberIds(new ArrayList<>(memberSet));

            List<WithdrawExcelVO> voList = new ArrayList<>();


            allOut.forEach(v -> {
                WithdrawExcelVO vo = new WithdrawExcelVO();
                BeanUtils.copyProperties(v, vo);

                vo.setMemberId(Long.valueOf(v.getMemberid()));

                vo.setMoney(String.valueOf(v.getMoney()));

                vo.setFee(String.valueOf(v.getFee()));

                vo.setReal_money(String.valueOf(v.getReal_money()));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                vo.setAddtime(sdf.format(new Date(v.getAddtime())));
                if (v.getProcesstime() != null && v.getProcesstime() > 0) {
                    vo.setProcesstime(sdf.format(new Date(v.getProcesstime())));
                } else {
                    vo.setProcesstime("--");
                }

                Integer statusD = v.getStatus();
                String statusStr = "";
                if (statusD == -1) {
                    statusStr = "驳回";
                } else if (statusD == 0) {
                    statusStr = "待处理";
                } else if (statusD == 1) {
                    statusStr = "处理中";
                } else if (statusD == 2) {
                    statusStr = "已处理";
                } else {
                    statusStr = "失败";
                }
                vo.setStatus(statusStr);

                Long memberId = vo.getMemberId();
                if (memberMap.containsKey(memberId)) {
                    Member member = memberMap.get(memberId);
                    vo.setEmail(member.getEmail());
                    vo.setMobilePhone(member.getMobilePhone());
                }
                voList.add(vo);

            });

            ExcelUtil.listToExcel(voList, WithdrawExcelVO.class.getDeclaredFields(), response.getOutputStream());

            return null;

        }


        Page<Withdraw> all = withdrawService.findAll(predicate, pageModel.getPageable());

        List<Long> memberIds = all.getContent().stream().map(v -> (long) v.getMemberid()).collect(Collectors.toList());
        Map<Long, Member> memberMap = memberService.mapByMemberIds(memberIds);

        Page<WithdrawVO> page = all.map(v -> {
            WithdrawVO withdrawVO = new WithdrawVO();
            BeanUtils.copyProperties(v, withdrawVO);
            Long memberid = (long) withdrawVO.getMemberid();
            if (memberMap.containsKey(memberid)) {
                withdrawVO.setUsername(memberMap.get(memberid).getUsername());
                withdrawVO.setEmail(memberMap.get(memberid).getEmail());
            }
            return withdrawVO;
        });

        return success(page);
    }


    @RequiresPermissions("finance:withdraw:merge")
    @PostMapping("/merge")
    @AccessLog(module = AdminModule.FINANCE, operation = "审核/驳回提币")
    public MessageResult merge(@Valid Withdraw withdraw, BindingResult bindingResult) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }

        if (withdraw.getId() == null || withdraw.getId() <= 0) {
            result = error("请选择审核的记录");
            return result;
        }

        Withdraw one = withdrawService.findOne(withdraw.getId());
        if (one == null) {
            result = error("请选择审核的记录");
            return result;
        }

        Withdraw update = new Withdraw();
        update.setId(withdraw.getId());
        update.setStatus(withdraw.getStatus());
        if (withdraw.getStatus() == -1) {
            update.setWithdrawinfo(withdraw.getWithdrawinfo());
        }

        if (StringUtils.isBlank(withdraw.getWithdrawinfo())) {
            update.setWithdrawinfo("");
        }
        update.setProcesstime(new Date().getTime());
        update.setCoinname(one.getCoinname());
        update.setMemberid(one.getMemberid());
        update.setMoney(one.getMoney());
        withdrawService.save(update);

        result = success("操作成功");
        result.setData(withdraw);
        return result;
    }

}
