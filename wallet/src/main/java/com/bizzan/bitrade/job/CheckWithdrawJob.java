package com.bizzan.bitrade.job;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.Coinprotocol;
import com.bizzan.bitrade.entity.Withdraw;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.CoinprotocolService;
import com.bizzan.bitrade.service.WithdrawService;
import com.bizzan.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 检查用户提现申请
 * @author Hevin E-Mali:390330302@qq.com
 *
 */
@Component
@Slf4j
public class CheckWithdrawJob {
	
	@Autowired
    private WithdrawService withdrawService;
	@Autowired
	private CoinprotocolService coinprotocolService;
	@Autowired
	private CoinService coinService;
	@Autowired
	private RestTemplate restTemplate;

    
	/**
	 * 每小时检查一次
	 */
	@Scheduled(cron = "0 */2 * * * *")
    public void checkNewWithdrawApplication(){
		log.info("开始处理待打币订单！");
		// 获取打币的订单
		List<Withdraw> list = withdrawService.findWithrawByStatus(1);
		if(list!=null && list.size()>0){
			for (Withdraw withdraw : list) {
				log.info("处理：：："+withdraw.getId());
				withdrawService.updateWithdrawStatus(withdraw.getId(),2);
				handleWithdraw(withdraw);
			}
		}

	}

	private void handleWithdraw(Withdraw record) {
		if (record==null || record.getStatus()!=1 ) {
			return;
		}
		Coinprotocol protocol = coinprotocolService.findByProtocol(record.getProtocol());
		Integer withdrawId = record.getId();
		try {
			String serviceName = "SERVICE-RPC-" + protocol.getSymbol().toUpperCase();
			String url = "http://" + serviceName + "/rpc/withdraw?address={1}&amount={2}&coinName={3}";

			Coin coin = coinService.findOne(record.getCoinname());

			log.info("coin = {}",coin.toString());
			if (coin != null && coin.getCanWithdraw() == BooleanEnum.IS_TRUE) {
				MessageResult result = restTemplate.getForObject(url,
						MessageResult.class, record.getAddress(), record.getReal_money(), record.getCoinname());
				log.info("=========================rpc 结束================================");
				log.info("result = {}", result);
				if (result.getCode() == 0 && result.getData() != null) {
					log.info("====================== 处理成功,data为txid更新业务 ==================================");
					//处理成功,data为txid，更新业务订单
					String txid = (String) result.getData();
					withdrawService.withdrawSuccess(record,coin,txid);
				} else {
					log.info("====================== 自动转账失败，转为人工处理 ==================================");
					//自动转账失败，转为人工处理
					withdrawService.updateWithdrawStatus(withdrawId,3);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("auto withdraw failed,error={}", e.getMessage());
			log.info("====================== 自动转账失败，转为人工处理 ==================================");
			//自动转账失败，转为人工处理
			withdrawService.updateWithdrawStatus(withdrawId,3);
		}
	}
	

}