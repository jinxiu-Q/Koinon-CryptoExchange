package com.bizzan.bitrade.controller;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.constant.WithdrawStatus;
import com.bizzan.bitrade.dto.CoinextDTO;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.exception.InformationExpiredException;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.system.CoinExchangeFactory;
import com.bizzan.bitrade.util.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.bizzan.bitrade.constant.SysConstant.*;
import static com.bizzan.bitrade.util.BigDecimalUtils.compare;
import static com.bizzan.bitrade.util.BigDecimalUtils.sub;
import static com.bizzan.bitrade.util.MessageResult.error;
import static com.bizzan.bitrade.util.MessageResult.success;
import static org.springframework.util.Assert.*;

/**
 * @author Hevin E-Mali:390330302@qq.com
 * @date 2021年01月26日
 */
@RestController
@Slf4j
@RequestMapping(value = "/withdraw", method = RequestMethod.POST)
public class WithdrawController {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spark.system.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String from;
    @Value("${spark.system.name}")
    private String company;
    @Autowired
    private MemberAddressService memberAddressService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private WithdrawRecordService withdrawApplyService;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private LocaleMessageSourceService sourceService;
    @Resource
    private LocaleMessageSourceService localeMessageSourceService;

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    private CoinextService coinextService;

    @Autowired
    private CoinExchangeFactory coinExchangeFactory;

    /**
     * 增加提现地址
     *
     * @param address
     * @param unit
     * @param remark
     * @param user
     * @return
     */
    @RequestMapping("address/add")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult addAddress(String address, String unit, String remark, String fundpwd, @SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        hasText(address, sourceService.getMessage("MISSING_COIN_ADDRESS"));
        hasText(unit, sourceService.getMessage("MISSING_COIN_TYPE"));
        hasText(fundpwd, sourceService.getMessage("MISSING_JYPASSWORD"));
        Member member = memberService.findOne(user.getId());
        String mbPassword = member.getJyPassword();
        Assert.hasText(mbPassword, sourceService.getMessage("NO_SET_JYPASSWORD"));
        Assert.isTrue(Md5.md5Digest(fundpwd + member.getSalt()).toLowerCase().equals(mbPassword), sourceService.getMessage("ERROR_JYPASSWORD"));
        MessageResult result = memberAddressService.addMemberAddress(user.getId(), address, unit, remark, 0, "");
        if (result.getCode() == 0) {
            result.setMessage(sourceService.getMessage("ADD_ADDRESS_SUCCESS"));
        } else if (result.getCode() == 500) {
            result.setMessage(sourceService.getMessage("ADD_ADDRESS_FAILED"));
        } else if (result.getCode() == 600) {
            result.setMessage(sourceService.getMessage("COIN_NOT_SUPPORT"));
        }
        return result;
    }

    @RequestMapping("address/add_app")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult addAddressApp(String address, String unit, String remark, Integer protocal, @SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        hasText(address, sourceService.getMessage("MISSING_COIN_ADDRESS"));
        hasText(unit, sourceService.getMessage("MISSING_COIN_TYPE"));
        String confirmationString = "";
        try {
            String src = user.getId() + address + unit + remark + protocal + "" + System.currentTimeMillis();
            confirmationString = Md5.md5Digest(src);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        MessageResult result = memberAddressService.addMemberAddress(user.getId(), address, unit, remark, protocal, confirmationString);
        if (result.getCode() == 0) {
            sendRegEmailForApp(user.getEmail(), confirmationString);
            result.setMessage(sourceService.getMessage("ADD_ADDRESS_SUCCESS"));
        } else if (result.getCode() == 500) {
            result.setMessage(sourceService.getMessage("ADD_ADDRESS_FAILED"));
        } else if (result.getCode() == 600) {
            result.setMessage(sourceService.getMessage("COIN_NOT_SUPPORT"));
        }
        return result;
    }

    public MessageResult sendRegEmailForApp(String email, String confirmationString) {
        Assert.isTrue(ValidateUtil.isEmail(email), localeMessageSourceService.getMessage("WRONG_EMAIL"));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        try {
            sentEmailAddCode("Confirm your new withdrawal address", email, confirmationString, "withdrawaladdress.ftl");
            valueOperations.set(EMAIL_WITHDRAW_ADDRESS_PREFIX + email, confirmationString, 60, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
            return error(localeMessageSourceService.getMessage("SEND_FAILED"));
        }
        return success(localeMessageSourceService.getMessage("SENT_SUCCESS_TEN"));
    }

    @Async
    public void sentEmailAddCode(String subject, String email, String string, String flt) throws MessagingException, IOException, TemplateException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(from, company);
        helper.setTo(email);
        helper.setSubject(subject);
        Map<String, Object> model = new HashMap<>(16);
        model.put("code", string);
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
        cfg.setClassForTemplateLoading(this.getClass(), "/templates");
        Template template = cfg.getTemplate(flt);
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        helper.setText(html, true);
        //发送邮件
        javaMailSender.send(mimeMessage);
    }

    @GetMapping("approve")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult addAddressapprove(String code) throws Exception {
        MemberAddress memberAddress = memberAddressService.findByConfirmationString(code);
        notNull(memberAddress, localeMessageSourceService.getMessage("VERIFICATION_CODE_NOT_EXISTS"));
        Member member = memberService.findOne(memberAddress.getMemberId());
        notNull(member, localeMessageSourceService.getMessage("VERIFICATION_CODE_NOT_EXISTS"));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object srcCode = valueOperations.get(SysConstant.EMAIL_WITHDRAW_ADDRESS_PREFIX + member.getEmail());
        notNull(srcCode, localeMessageSourceService.getMessage("VERIFICATION_CODE_NOT_EXISTS"));
        if (!srcCode.toString().equals(code)) {
            return error(localeMessageSourceService.getMessage("VERIFICATION_CODE_INCORRECT"));
        } else {
            valueOperations.getOperations().delete(SysConstant.EMAIL_WITHDRAW_ADDRESS_PREFIX + member.getEmail());
        }
        MessageResult result = memberAddressService.modifyMemberAddressStatus(memberAddress);
        if (result.getCode() == 0) {
            result.setMessage(sourceService.getMessage("ADD_ADDRESS_SUCCESS"));
        } else {
            result.setMessage(sourceService.getMessage("ADD_ADDRESS_FAILED"));
        }
        return result;
    }


    /**
     * 删除提现地址
     *
     * @param id
     * @param user
     * @return
     */
    @RequestMapping("address/delete")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult deleteAddress(long id, @SessionAttribute(SESSION_MEMBER) AuthMember user) {
        MessageResult result = memberAddressService.deleteMemberAddress(user.getId(), id);
        if (result.getCode() == 0) {
            result.setMessage(sourceService.getMessage("DELETE_ADDRESS_SUCCESS"));
        } else {
            result.setMessage(sourceService.getMessage("DELETE_ADDRESS_FAILED"));
        }
        return result;
    }

    /**
     * 提现地址分页信息
     *
     * @param user
     * @param pageNo
     * @param pageSize
     * @param unit
     * @return
     */
    @RequestMapping("address/page")
    public MessageResult addressPage(@SessionAttribute(SESSION_MEMBER) AuthMember user, int pageNo, int pageSize, String unit) {
        Page<MemberAddress> page = memberAddressService.pageQuery(pageNo, pageSize, user.getId(), unit);
        Page<ScanMemberAddress> scanMemberAddresses = page.map(x -> ScanMemberAddress.toScanMemberAddress(x));
        MessageResult result = MessageResult.success();
        result.setData(scanMemberAddresses);
        return result;
    }

    @RequestMapping("address/list")
    public MessageResult addressPage(@SessionAttribute(SESSION_MEMBER) AuthMember user, String unit) {
        Coin coin = coinService.findByUnit(unit);
        notNull(coin, sourceService.getMessage("COIN_ILLEGAL"));
        List<Map<String, String>> list1 = memberAddressService.queryAddress(user.getId(), coin.getName());
        List<Map<String, String>> list2 = memberAddressService.queryAddressWaiting(user.getId(), coin.getName());
        list1.addAll(list2);
        MessageResult result = MessageResult.success();
        result.setData(list1);
        return result;
    }

    @RequestMapping("address/modify")
    public MessageResult addressModify(@SessionAttribute(SESSION_MEMBER) AuthMember user, Long id, String remark) {
        MessageResult result = memberAddressService.modifyMemberAddress(user.getId(), id, remark);
        if (result.getCode() == 0) {
            result.setMessage(sourceService.getMessage("DELETE_ADDRESS_SUCCESS"));
        } else {
            result.setMessage(sourceService.getMessage("DELETE_ADDRESS_FAILED"));
        }
        return result;
    }

    /**
     * 支持提现的地址
     *
     * @return
     */
    @RequestMapping("support/coin")
    public MessageResult queryWithdraw() {
        List<Coin> list = coinService.findAllCanWithDraw();
        List<String> list1 = new ArrayList<>();
        list.stream().forEach(x -> list1.add(x.getUnit()));
        MessageResult result = MessageResult.success();
        result.setData(list1);
        return result;
    }

    /**
     * 会员操作前，选择界面,
     * 下发可以提现的资金列表，及相关支持的链
     */

    @RequestMapping("support/list")
    public MessageResult supportWithdrawCode(@SessionAttribute(SESSION_MEMBER) AuthMember user) {
        MessageResult mr = new MessageResult(0, "success");
        List<MemberWallet> records = memberWalletService.findAllByMemberId(user.getId());
        List<MemberWallet> addList = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        for (MemberWallet wallet : records) {
            if (wallet.getBalance().compareTo(BigDecimal.ZERO) > 0 && wallet.getCoin().getCanWithdraw() == BooleanEnum.IS_TRUE) {
                addList.add(wallet);
                //
                CoinExchangeFactory.ExchangeRate rate = coinExchangeFactory.get(wallet.getCoin().getUnit());
                if (rate != null) {
                    wallet.getCoin().setUsdRate(rate.getUsdRate().doubleValue());
                    wallet.getCoin().setCnyRate(rate.getCnyRate().doubleValue());
                    wallet.getCoin().setJpyRate(rate.getJpyRate().doubleValue());
                    wallet.getCoin().setEurRate(rate.getEurRate().doubleValue());
                    wallet.getCoin().setHkdRate(rate.getHkdRate().doubleValue());
                    wallet.getCoin().setGbpRate(rate.getGbpRate().doubleValue());
                    wallet.getCoin().setAudRate(rate.getAudRate().doubleValue());
                    wallet.getCoin().setCadRate(rate.getCadRate().doubleValue());
                    wallet.getCoin().setAedRate(rate.getAedRate().doubleValue());
                } else {
                    log.info("unit = {} , rate = null ", wallet.getCoin().getUnit());
                }
                //协议
                List<CoinextDTO> protocolList = coinextService.findByCoinname(wallet.getCoin().getName());
                map.put("" + wallet.getCoin().getUnit(), protocolList);
            }
        }
        HashMap<String, Object> mapresult = new HashMap<>();
        mapresult.put("wallet", addList);
        mapresult.put("protocol", map);
        mr.setData(mapresult);
        return mr;
    }

    /**
     * 提现币种详细信息
     *
     * @param user
     * @return
     */
    @RequestMapping("support/coin/info")
    public MessageResult queryWithdrawCoin(@SessionAttribute(SESSION_MEMBER) AuthMember user) {
        List<Coin> list = coinService.findAllCanWithDraw();
        List<MemberWallet> list1 = memberWalletService.findAllByMemberId(user.getId());
        long id = user.getId();
        List<WithdrawWalletInfo> list2 = list1.stream().filter(x -> list.contains(x.getCoin())).map(x ->
                WithdrawWalletInfo.builder()
                        .balance(x.getBalance())
                        .withdrawScale(x.getCoin().getWithdrawScale())
                        .maxTxFee(x.getCoin().getMaxTxFee())
                        .minTxFee(x.getCoin().getMinTxFee())
                        .minAmount(x.getCoin().getMinWithdrawAmount())
                        .maxAmount(x.getCoin().getMaxWithdrawAmount())
                        .name(x.getCoin().getName())
                        .nameCn(x.getCoin().getNameCn())
                        .threshold(x.getCoin().getWithdrawThreshold())
                        .unit(x.getCoin().getUnit())
                        .accountType(x.getCoin().getAccountType())
                        .canAutoWithdraw(x.getCoin().getCanAutoWithdraw())
                        .addresses(memberAddressService.queryAddress(id, x.getCoin().getName())).build()
        ).collect(Collectors.toList());
        MessageResult result = MessageResult.success();
        result.setData(list2);
        return result;
    }


    /**
     * 申请提币(请到PC端提币或升级APP)
     * 没有验证码校验
     *
     * @param user
     * @param unit
     * @param address
     * @param amount
     * @param fee
     * @param remark
     * @param jyPassword
     * @return
     * @throws Exception
     */
    @RequestMapping("apply")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult withdraw(@SessionAttribute(SESSION_MEMBER) AuthMember user, String unit, String address,
                                  BigDecimal amount, BigDecimal fee, String remark, String jyPassword) throws Exception {
        return MessageResult.success(sourceService.getMessage("WITHDRAW_TO_PC"));
    }

    /**
     * 申请提币（添加验证码校验）
     *
     * @param user
     * @param unit
     * @param address
     * @param amount
     * @param fee
     * @param remark
     * @param jyPassword
     * @return
     * @throws Exception
     */
    @RequestMapping("apply/code")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult withdrawCode(@SessionAttribute(SESSION_MEMBER) AuthMember user, String unit, String address,
                                      BigDecimal amount, BigDecimal fee, String remark, String jyPassword) throws Exception {
        hasText(jyPassword, sourceService.getMessage("MISSING_JYPASSWORD"));
        hasText(unit, sourceService.getMessage("MISSING_COIN_TYPE"));
        Coin coin = coinService.findByUnit(unit);
        amount.setScale(coin.getWithdrawScale(), BigDecimal.ROUND_DOWN);
        notNull(coin, sourceService.getMessage("COIN_ILLEGAL"));

        isTrue(coin.getStatus().equals(CommonStatus.NORMAL) && coin.getCanWithdraw().equals(BooleanEnum.IS_TRUE), sourceService.getMessage("COIN_NOT_SUPPORT"));
        isTrue(compare(fee, new BigDecimal(String.valueOf(coin.getMinTxFee()))), sourceService.getMessage("CHARGE_MIN") + coin.getMinTxFee());
        isTrue(compare(new BigDecimal(String.valueOf(coin.getMaxTxFee())), fee), sourceService.getMessage("CHARGE_MAX") + coin.getMaxTxFee());
        isTrue(compare(coin.getMaxWithdrawAmount(), amount), sourceService.getMessage("WITHDRAW_MAX") + coin.getMaxWithdrawAmount());
        isTrue(compare(amount, coin.getMinWithdrawAmount()), sourceService.getMessage("WITHDRAW_MIN") + coin.getMinWithdrawAmount());
        MemberWallet memberWallet = memberWalletService.findByCoinAndMemberId(coin, user.getId());
        isTrue(compare(memberWallet.getBalance(), amount), sourceService.getMessage("INSUFFICIENT_BALANCE"));
        isTrue(memberWallet.getIsLock() == BooleanEnum.IS_FALSE, "The wallet is locked.");
        Member member = memberService.findOne(user.getId());
        String mbPassword = member.getJyPassword();
        Assert.hasText(mbPassword, sourceService.getMessage("NO_SET_JYPASSWORD"));
        Assert.isTrue(Md5.md5Digest(jyPassword + member.getSalt()).toLowerCase().equals(mbPassword), sourceService.getMessage("ERROR_JYPASSWORD"));

        MessageResult result = memberWalletService.freezeBalance(memberWallet, amount);
        if (result.getCode() != 0) {
            throw new InformationExpiredException("Information Expired");
        }
        WithdrawRecord withdrawApply = new WithdrawRecord();
        withdrawApply.setCoin(coin);
        withdrawApply.setFee(fee);
        withdrawApply.setArrivedAmount(sub(amount, fee));
        withdrawApply.setMemberId(user.getId());
        withdrawApply.setTotalAmount(amount);
        withdrawApply.setAddress(address);
        withdrawApply.setRemark(remark);
        withdrawApply.setCanAutoWithdraw(coin.getCanAutoWithdraw());

        //提币数量低于或等于阈值并且该币种支持自动提币
        if (amount.compareTo(coin.getWithdrawThreshold()) <= 0 && coin.getCanAutoWithdraw().equals(BooleanEnum.IS_TRUE)) {
            withdrawApply.setStatus(WithdrawStatus.WAITING);
            withdrawApply.setIsAuto(BooleanEnum.IS_TRUE);
            withdrawApply.setDealTime(withdrawApply.getCreateTime());
            WithdrawRecord withdrawRecord = withdrawApplyService.save(withdrawApply);
            JSONObject json = new JSONObject();
            json.put("uid", user.getId());
            //提币总数量
            json.put("totalAmount", amount);
            //手续费
            json.put("fee", fee);
            //预计到账数量
            json.put("arriveAmount", sub(amount, fee));
            //币种
            json.put("coin", coin);
            //提币地址
            json.put("address", address);
            //提币记录id
            json.put("withdrawId", withdrawRecord.getId());
            kafkaTemplate.send("withdraw", coin.getUnit(), json.toJSONString());
            return MessageResult.success(sourceService.getMessage("APPLY_SUCCESS"));
        } else {
            withdrawApply.setStatus(WithdrawStatus.PROCESSING);
            withdrawApply.setIsAuto(BooleanEnum.IS_FALSE);
            if (withdrawApplyService.save(withdrawApply) != null) {
                return MessageResult.success(sourceService.getMessage("APPLY_AUDIT"));
            } else {
                throw new InformationExpiredException("Information Expired");
            }
        }
    }


    /**
     * 提币记录
     *
     * @param user
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("record")
    public MessageResult pageWithdraw(@SessionAttribute(SESSION_MEMBER) AuthMember user, int page, int pageSize) {
        MessageResult mr = new MessageResult(0, "success");
        Page<WithdrawRecord> records = withdrawApplyService.findAllByMemberId(user.getId(), page, pageSize);
        records.map(x -> ScanWithdrawRecord.toScanWithdrawRecord(x));
        mr.setData(records);
        return mr;
    }


    /**
     * 提币记录
     */
    @GetMapping("list")
    public MessageResult list(@SessionAttribute(SESSION_MEMBER) AuthMember user, int page, int pageSize) {
        MessageResult mr = new MessageResult(0, "success");
        Page<Withdraw> records = withdrawService.findAllByMemberId((int) user.getId(), page, pageSize);
        mr.setData(records);
        return mr;
    }


    /**
     * 提币
     *
     * @return
     */
    @PostMapping("create")
    public MessageResult create(@SessionAttribute(SESSION_MEMBER) AuthMember user,
                                @RequestParam(value = "coinName") String coinName,
                                @RequestParam(value = "coinprotocol") Integer coinprotocol,
                                @RequestParam(value = "address") String address,
                                @RequestParam(value = "money") double money,
                                @RequestParam(value = "code") String code,
                                @RequestParam(value = "codeType") Integer codeType,
                                @RequestParam(value = "payPwd") String payPwd) throws Exception {


        Long memberid = user.getId();
        // 查询资金密码
        Member member = memberService.findOne(user.getId());
        isTrue(Md5.md5Digest(payPwd + member.getSalt()).toLowerCase().equals(member.getJyPassword()), sourceService.getMessage("ERROR_JYPASSWORD"));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //验证验证码
        if (codeType == 1) {
            //邮箱
            String email = member.getEmail();
            Object redisCode = valueOperations.get(SysConstant.EMAIL_WITHDRAW_MONEY_CODE_PREFIX + email);
            notNull(redisCode, sourceService.getMessage("VERIFICATION_CODE_NOT_EXISTS"));
            if (!redisCode.toString().equals(code)) {
                return error(sourceService.getMessage("VERIFICATION_CODE_INCORRECT"));
            } else {
                valueOperations.getOperations().delete(SysConstant.EMAIL_WITHDRAW_MONEY_CODE_PREFIX + email);
            }
        } else if (codeType == 2) {
            //手机
            String key = SysConstant.PHONE_WITHDRAW_MONEY_CODE_PREFIX + member.getMobilePhone();
            Object redisCode = valueOperations.get(key);
            notNull(redisCode, sourceService.getMessage("VERIFICATION_CODE_NOT_EXISTS"));
            if (!redisCode.toString().equals(code)) {
                return error(sourceService.getMessage("VERIFICATION_CODE_INCORRECT"));
            } else {
                valueOperations.getOperations().delete(key);
            }
        } else {
            //Google
            long t = System.currentTimeMillis();
            GoogleAuthenticatorUtil ga = new GoogleAuthenticatorUtil();
            //  ga.setWindowSize(0); // should give 5 * 30 seconds of grace...
            boolean r = ga.check_code(member.getGoogleKey(), Long.valueOf(code), t);
            if (!r) {
                return error(sourceService.getMessage("VERIFICATION_CODE_INCORRECT"));
            }
        }

        // 查询币种配置
        Coinext firstByCoinnameAndProtocol = coinextService.findFirstByCoinnameAndProtocol(coinName, coinprotocol);

        if (firstByCoinnameAndProtocol == null) {
            return MessageResult.error(sourceService.getMessage("COIN_ILLEGAL"));
        }

        Integer iswithdraw = firstByCoinnameAndProtocol.getIswithdraw();
        if (iswithdraw != 1) {
            return MessageResult.error(sourceService.getMessage("COIN_NOT_SUPPORT"));
        }
        Integer decimals = firstByCoinnameAndProtocol.getDecimals();
        Integer isautowithdraw = firstByCoinnameAndProtocol.getIsautowithdraw();

        // 保留两位小数
        BigDecimal bigDecimalMoney = new BigDecimal(money).setScale(decimals, BigDecimal.ROUND_DOWN);

        BigDecimal bigDecimalMinwithdraw = BigDecimal.valueOf(firstByCoinnameAndProtocol.getMinwithdraw());
        BigDecimal bigDecimalMaxwithdraw = BigDecimal.valueOf(firstByCoinnameAndProtocol.getMaxwithdraw());
        BigDecimal bigDecimalWithdrawfee = BigDecimal.valueOf(firstByCoinnameAndProtocol.getWithdrawfee());
        BigDecimal bigDecimalMinwithdrawfee = BigDecimal.valueOf(firstByCoinnameAndProtocol.getMinwithdrawfee());

        // 如果提现金额为0或者负数
        if (bigDecimalMoney.compareTo(new BigDecimal(0)) <= 0) {
            return MessageResult.error(sourceService.getMessage("WITHDRAW_MIN") + "0");
        }

        // 如果提现金额小于最低提现数量
        if (bigDecimalMoney.compareTo(bigDecimalMinwithdraw) < 0) {
            return MessageResult.error(sourceService.getMessage("WITHDRAW_MIN") + bigDecimalMinwithdraw.toPlainString());
        }

        // 如果提现金额大于最大金额
        if (bigDecimalMoney.compareTo(bigDecimalMaxwithdraw) > 0) {
            return MessageResult.error(sourceService.getMessage("WITHDRAW_MAX") + bigDecimalMaxwithdraw.toPlainString());
        }

        BigDecimal fee = bigDecimalMoney.multiply(bigDecimalWithdrawfee).setScale(decimals, BigDecimal.ROUND_DOWN);
        // 如果手续费小于最低手续费则使用最低手续费
        if (fee.compareTo(bigDecimalMinwithdrawfee) < 0) {
            fee = bigDecimalMinwithdrawfee;
        }

        BigDecimal Real_Money = bigDecimalMoney.subtract(fee).setScale(decimals, BigDecimal.ROUND_DOWN);

        // 如果实际到账为0或者负数
        if (Real_Money.compareTo(new BigDecimal(0)) <= 0) {
            return MessageResult.error(sourceService.getMessage("WITHDRAW_MIN"));
        }
        Coin coin = coinService.findOne(coinName);
        MemberWallet memberWallet = memberWalletService.findByCoinAndMemberId(coin, memberid);
        isTrue(compare(memberWallet.getBalance(), bigDecimalMoney), sourceService.getMessage("INSUFFICIENT_BALANCE"));

        isTrue(memberWallet.getIsLock() == BooleanEnum.IS_FALSE, "钱包已锁定");


        Withdraw withdraw = new Withdraw();
        withdraw.setMemberid(memberid.intValue());
        withdraw.setAddtime(new Date().getTime());
        withdraw.setCoinid(0);
        withdraw.setCoinname(coinName);
        withdraw.setAddress(address);
        withdraw.setMoney(bigDecimalMoney.doubleValue());
        withdraw.setFee(fee.doubleValue());
        withdraw.setReal_money(Real_Money.doubleValue());
        withdraw.setProcessmold(0);
        withdraw.setHash("");
        withdraw.setStatus(isautowithdraw == 1 ? 1 : 0);
        withdraw.setProcesstime(0L);
        withdraw.setWithdrawinfo("");
        withdraw.setRemark("");
        withdraw.setProtocol(coinprotocol);
        withdraw.setProtocolname(firstByCoinnameAndProtocol.getProtocolname());

        withdrawService.create(withdraw);

        return MessageResult.success();
    }


    @PostMapping("create_app")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult createApp(@SessionAttribute(SESSION_MEMBER) AuthMember user, String unit, Integer coinprotocol,
                                   String address, BigDecimal amount, String remark) throws Exception {
        hasText(unit, sourceService.getMessage("MISSING_COIN_TYPE"));
        Coin coin = coinService.findByUnit(unit);
        MemberAddress memberAddress = memberAddressService.findByAddress(address);
        amount.setScale(coin.getWithdrawScale(), BigDecimal.ROUND_DOWN);
        notNull(coin, sourceService.getMessage("COIN_ILLEGAL"));
        MemberWallet memberWallet = memberWalletService.findByCoinAndMemberId(coin, user.getId());
        isTrue(memberWallet.getIsLock() == BooleanEnum.IS_FALSE, "The wallet is locked.");
        isTrue(user.getId() == memberAddress.getMemberId().longValue(), "The address is bing user.");
        //Member member = memberService.findOne(user.getId());
        // 查询币种配置
        Coinext firstByCoinnameAndProtocol = coinextService.findFirstByCoinnameAndProtocol(coin.getName(), coinprotocol);
        if (firstByCoinnameAndProtocol == null) {
            return MessageResult.error(sourceService.getMessage("COIN_ILLEGAL"));
        }
        Integer iswithdraw = firstByCoinnameAndProtocol.getIswithdraw();
        if (iswithdraw != 1) {
            return MessageResult.error(sourceService.getMessage("COIN_NOT_SUPPORT"));
        }
        Integer decimals = firstByCoinnameAndProtocol.getDecimals();
        Integer isautowithdraw = firstByCoinnameAndProtocol.getIsautowithdraw();
        // 保留两位小数
        BigDecimal bigDecimalMoney = amount.setScale(decimals, BigDecimal.ROUND_DOWN);
        BigDecimal bigDecimalMinwithdraw = BigDecimal.valueOf(firstByCoinnameAndProtocol.getMinwithdraw());
        BigDecimal bigDecimalMaxwithdraw = BigDecimal.valueOf(firstByCoinnameAndProtocol.getMaxwithdraw());
        BigDecimal bigDecimalWithdrawfee = BigDecimal.valueOf(firstByCoinnameAndProtocol.getWithdrawfee());
        BigDecimal bigDecimalMinwithdrawfee = BigDecimal.valueOf(firstByCoinnameAndProtocol.getMinwithdrawfee());
        // 如果提现金额为0或者负数
        if (bigDecimalMoney.compareTo(new BigDecimal(0)) <= 0) {
            return MessageResult.error(sourceService.getMessage("WITHDRAW_MIN") + "0");
        }
        // 如果提现金额小于最低提现数量
        if (bigDecimalMoney.compareTo(bigDecimalMinwithdraw) < 0) {
            return MessageResult.error(sourceService.getMessage("WITHDRAW_MIN") + bigDecimalMinwithdraw.toPlainString());
        }
        // 如果提现金额大于最大金额
        if (bigDecimalMoney.compareTo(bigDecimalMaxwithdraw) > 0) {
            return MessageResult.error(sourceService.getMessage("WITHDRAW_MAX") + bigDecimalMaxwithdraw.toPlainString());
        }
        BigDecimal fee = bigDecimalMoney.multiply(bigDecimalWithdrawfee).setScale(decimals, BigDecimal.ROUND_DOWN);
        // 如果手续费小于最低手续费则使用最低手续费
        if (fee.compareTo(bigDecimalMinwithdrawfee) < 0) {
            fee = bigDecimalMinwithdrawfee;
        }
        BigDecimal Real_Money = bigDecimalMoney.subtract(fee).setScale(decimals, BigDecimal.ROUND_DOWN);
        // 如果实际到账为0或者负数
        if (Real_Money.compareTo(new BigDecimal(0)) <= 0) {
            return MessageResult.error(sourceService.getMessage("WITHDRAW_MIN"));
        }

        MessageResult result = memberWalletService.freezeBalance(memberWallet, amount);
        if (result.getCode() != 0) {
            throw new InformationExpiredException("Information Expired");
        }



        WithdrawRecord withdrawApply = new WithdrawRecord();
        withdrawApply.setCoin(coin);
        withdrawApply.setFee(fee);
        withdrawApply.setArrivedAmount(sub(amount, fee));
        withdrawApply.setMemberId(user.getId());
        withdrawApply.setTotalAmount(amount);
        withdrawApply.setAddress(address);
        withdrawApply.setRemark(remark);
        withdrawApply.setCanAutoWithdraw(coin.getCanAutoWithdraw());
        withdrawApply.setProtocol(coinprotocol);
        withdrawApply.setProtocolname(firstByCoinnameAndProtocol.getProtocolname());
        //
        sentEmailAddCode("Your withdrawal is being processed", user.getEmail(), memberAddress.getRemark(), "withdrawal.ftl");
        //提币数量低于或等于阈值并且该币种支持自动提币
        if (isautowithdraw == 1) {
            withdrawApply.setStatus(WithdrawStatus.WAITING);
            withdrawApply.setIsAuto(BooleanEnum.IS_TRUE);
            withdrawApply.setDealTime(withdrawApply.getCreateTime());
            WithdrawRecord withdrawRecord = withdrawApplyService.save(withdrawApply);
            JSONObject json = new JSONObject();
            json.put("uid", user.getId());
            //提币总数量
            json.put("totalAmount", amount);
            //手续费
            json.put("fee", fee);
            //预计到账数量
            json.put("arriveAmount", sub(amount, fee));
            //币种
            json.put("coin", coin);
            //提币地址
            json.put("address", address);
            json.put("protocol", coinprotocol);
            //提币记录id
            json.put("withdrawId", withdrawRecord.getId());
            kafkaTemplate.send("withdraw", coin.getUnit(), json.toJSONString());
            return MessageResult.success(sourceService.getMessage("APPLY_SUCCESS"));
        } else {
            withdrawApply.setStatus(WithdrawStatus.PROCESSING);
            withdrawApply.setIsAuto(BooleanEnum.IS_FALSE);
            if (withdrawApplyService.save(withdrawApply) != null) {
                return MessageResult.success(sourceService.getMessage("APPLY_AUDIT"));
            } else {
                throw new InformationExpiredException("Information Expired");
            }
        }
    }
}
