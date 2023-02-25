package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.SmsDao;
import com.bizzan.bitrade.dto.SmsDTO;
import com.bizzan.bitrade.service.Base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date: create in 9:44 2019/6/28
 * @Modified:
 */
@Service
public class SmsService extends BaseService{
    
    @Autowired
    private SmsDao smsDao;

    /**
     * 获取有效的短信配置
     * @return
     */
    public SmsDTO getByStatus(){
        return smsDao.findBySmsStatus();
    }
    
    
}
