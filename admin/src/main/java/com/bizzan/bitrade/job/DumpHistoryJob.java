package com.bizzan.bitrade.job;

import com.bizzan.bitrade.entity.ExchangeOrder;
import com.bizzan.bitrade.entity.ExchangeOrderDetail;
import com.bizzan.bitrade.service.ExchangeOrderDetailService;
import com.bizzan.bitrade.service.ExchangeOrderService;
import com.bizzan.bitrade.service.MemberTransactionService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.*;

/**
 * 清理机器人订单（5天前订单）
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 *
 */
@Component
@Slf4j
public class DumpHistoryJob {

	@Autowired
	private ExchangeOrderService exchangeOrderService;

    @Autowired
    private ExchangeOrderDetailService exchangeOrderDetailService;

    @Autowired
    private MemberTransactionService memberTransactionService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MongoTemplate mongoTemplate ;

    @Value("${spring.mail.username}")
    private String from;
    @Value("${spark.system.host}")
    private String host;
    @Value("${spark.system.name}")
    private String company;

    @Value("${spark.system.admins}")
    private String admins;
    /**
     *  每天3点20分执行一次
     *  注意，删除订单主表和详情表时，只删除机器人数据（详见数据层的sql实现）
     * */
	@Scheduled(cron = "0 20 03 * * *")
	public void deleteHistoryOrders(){
		log.info("开始清理交易历史数据");
		long beforeTime = System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000); // 2天前
		log.info("清除指定时间之前的订单："+beforeTime);
		int limit = 1000;
		int deleteTime = 1;
		int deleteCount = 0;
		List<ExchangeOrder> list = exchangeOrderService.queryHistoryDelete(beforeTime,limit);
		boolean hashNext =true;
		while (hashNext) {
			hashNext = false;
			if (list != null && list.size() > 0) {
				deleteCount = deleteCount + list.size();
				Set<String> orderIds = new HashSet<>();
				for (int i = 0; i < list.size(); i++) {
					ExchangeOrder exchangeOrder = list.get(i);
					// 清除mongodb中的交易详细
					orderIds.add(exchangeOrder.getOrderId());
//					log.info("清理订单明细：" + exchangeOrder.getOrderId());
//					exchangeOrderService.delete(exchangeOrder.getOrderId());

				}
				//批量删除
				if(orderIds.size()>0) {
					log.info("批量清理订单数：" + orderIds.size());
					Query query = new Query(Criteria.where("orderId").in(orderIds));
					log.info("开始清理mongo：" + orderIds.size());
					mongoTemplate.remove(query, ExchangeOrderDetail.class);
					log.info("开始清理数据库：" + orderIds.size());
					exchangeOrderService.deleteInBatch(list);
					log.info("清理完成：" + orderIds.size());
				}

				log.info("第{}轮清除数据完成,已清除{}条数据",deleteTime,deleteCount);
				deleteTime++;
				if (list.size() == limit) {
					hashNext = true;
					log.info("获取第{}轮数据,进行数据清理",deleteTime);
					list = exchangeOrderService.queryHistoryDelete(beforeTime,limit);
				}
			}
		}
		log.info("清除交易订单：" + deleteCount + "条");

		Date today = new Date();
		Calendar now =Calendar.getInstance();
		now.setTime(today);
		now.set(Calendar.DATE,now.get(Calendar.DATE) - 5); // 清理5天前数据
		Date startTime = now.getTime();

		log.info("清除资产变更记录时间：" + startTime.getTime());

		int tCount = memberTransactionService.deleteHistory(startTime);
		memberTransactionService.deleteWalletHistory(startTime);

		log.info("清除资产变更记录数量：" + tCount);
		// 发送通知邮件
//		String[] adminList = admins.split(",");
//		for(int i = 0; i < adminList.length; i++) {
//			try {
//				sendEmailMsg(adminList[i], "清理机器人订单（ 共" + deleteCount+ "条 ），"
//										 + "清理机器人资产记录共（"+tCount+"）", "清理交易订单");
//			} catch (MessagingException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (TemplateException e) {
//				e.printStackTrace();
//			}
//		}

		log.info("结束清理交易历史数据");
	}
    // 删除资产变更中的数据（保留9天，代码删除）
    // delete from member_transaction where create_time < '2019-10-15 14:22:49' and type = 3 and member_id = 1

    // 删除钱包历史变化数据（保留9天，手动删除）
    // delete from member_wallet_history where op_time < '2019-10-15 14:22:49' and member_id=1

    @Async
    public void sendEmailMsg(String email, String msg, String subject) throws MessagingException, IOException, TemplateException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(from);
        helper.setTo(email);
        helper.setSubject(company + "-" + subject);
        Map<String, Object> model = new HashMap<>(16);
        model.put("msg", msg);
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
        cfg.setClassForTemplateLoading(this.getClass(), "/templates");
        Template template = cfg.getTemplate("simpleMessage.ftl");
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        helper.setText(html, true);

        //发送邮件
        javaMailSender.send(mimeMessage);
    }
}
