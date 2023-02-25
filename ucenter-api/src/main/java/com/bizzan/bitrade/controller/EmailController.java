package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.util.GeneratorUtil;
import com.bizzan.bitrade.util.MessageResult;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.bizzan.bitrade.constant.SysConstant.EMAIL_WITHDRAW_MONEY_CODE_PREFIX;
import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;
import static com.bizzan.bitrade.util.MessageResult.error;
import static com.bizzan.bitrade.util.MessageResult.success;

/**
 * @author Hevin E-Mali:390330302@qq.com
 * @date 2021年01月08日
 */
@Slf4j
@RestController
@RequestMapping("/email")
public class EmailController {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;
    @Value("${spark.system.host}")
    private String host;
    @Value("${spark.system.name}")
    private String company;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MemberService memberService;
    @Resource
    private LocaleMessageSourceService localeMessageSourceService;

    /**
     * 重置交易密码验证码
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/transaction/code", method = RequestMethod.POST)
    public MessageResult sendResetTransactionCode(@SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        Member member = memberService.findOne(user.getId());
        String email = member.getEmail();
        Assert.hasText(member.getEmail(), localeMessageSourceService.getMessage("NOT_BIND_EMAIL"));
        String randomCode = String.valueOf(GeneratorUtil.getRandomNumber(100000, 999999));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String key = SysConstant.EMAIL_TRANSACTION_CODE_PREFIX + email;
        if (valueOperations.get(key) != null) {
            return error(localeMessageSourceService.getMessage("EMAIL_ALREADY_SEND"));
        }
        try {
            sentEmailRegCode(valueOperations, email, randomCode);
            valueOperations.set(key, randomCode, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
            return error(localeMessageSourceService.getMessage("SEND_FAILED"));
        }
        return success(localeMessageSourceService.getMessage("SENT_SUCCESS_TEN"));
    }

    /**
     * 提币验证码
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/withdraw/code", method = RequestMethod.POST)
    public MessageResult withdrawCode(@SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        Member member = memberService.findOne(user.getId());
        String email = member.getEmail();
        Assert.hasText(member.getEmail(), localeMessageSourceService.getMessage("NOT_BIND_EMAIL"));
        String randomCode = String.valueOf(GeneratorUtil.getRandomNumber(100000, 999999));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String key = EMAIL_WITHDRAW_MONEY_CODE_PREFIX + email;
        if (valueOperations.get(key) != null) {
            return error(localeMessageSourceService.getMessage("EMAIL_ALREADY_SEND"));
        }
        try {
            sentEmailRegCode(valueOperations, email, randomCode);
            valueOperations.set(key, randomCode, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
            return error(localeMessageSourceService.getMessage("SEND_FAILED"));
        }
        return success(localeMessageSourceService.getMessage("SENT_SUCCESS_TEN"));
    }

    /**
     * 更改登录密码验证码
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/update/password/code", method = RequestMethod.POST)
    public MessageResult updatePasswordCode(@SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        Member member = memberService.findOne(user.getId());
        String email = member.getEmail();
        Assert.hasText(member.getEmail(), localeMessageSourceService.getMessage("NOT_BIND_EMAIL"));
        String randomCode = String.valueOf(GeneratorUtil.getRandomNumber(100000, 999999));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String key = SysConstant.EMAIL_UP_PWD_CODE_PREFIX + email;
        if (valueOperations.get(key) != null) {
            return error(localeMessageSourceService.getMessage("EMAIL_ALREADY_SEND"));
        }
        try {
            sentEmailRegCode(valueOperations, email, randomCode);
            valueOperations.set(key, randomCode, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
            return error(localeMessageSourceService.getMessage("SEND_FAILED"));
        }
        return success(localeMessageSourceService.getMessage("SENT_SUCCESS_TEN"));
    }


    /**
     * 普通发送邮件
     * @param valueOperations
     * @param email
     * @param code
     * @throws MessagingException
     * @throws IOException
     * @throws TemplateException
     */
    @Async
    public void sentEmailRegCode(ValueOperations valueOperations, String email, String code) throws MessagingException, IOException, TemplateException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(from);
        helper.setTo(email);
        helper.setSubject(company);
        Map<String, Object> model = new HashMap<>(16);
        model.put("code", code);
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
        cfg.setClassForTemplateLoading(this.getClass(), "/templates");
        Template template = cfg.getTemplate("bindCodeEmail.ftl");
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        helper.setText(html, true);
        //发送邮件
        javaMailSender.send(mimeMessage);
    }

}
