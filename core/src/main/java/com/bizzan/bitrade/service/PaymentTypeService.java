package com.bizzan.bitrade.service;

import com.alibaba.fastjson.JSON;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.dao.CoinDao;
import com.bizzan.bitrade.dao.OtcCoinDao;
import com.bizzan.bitrade.dao.PaymentTypeDao;
import com.bizzan.bitrade.dao.PaymentTypeRecordDao;
import com.bizzan.bitrade.dto.CoinDTO;
import com.bizzan.bitrade.dto.ContractDTO;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.OtcCoin;
import com.bizzan.bitrade.entity.PaymentType;
import com.bizzan.bitrade.entity.QCoin;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.PageResult;
import com.bizzan.bitrade.service.Base.BaseService;
import com.bizzan.bitrade.vo.PaymentTypeConfig;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bizzan.bitrade.constant.BooleanEnum.IS_FALSE;
import static com.bizzan.bitrade.constant.BooleanEnum.IS_TRUE;

/**
 * @author Hevin E-Mali:390330302@qq.com
 * @description
 * @date 2021/12/29 14:50
 */
@Service
public class PaymentTypeService extends BaseService {
    @Autowired
    private PaymentTypeDao paymentTypeDao;
    @Autowired
    private LocaleMessageSourceService msService;


    @Override
    public List<PaymentType> findAll() {
        List<PaymentType> list = paymentTypeDao.findAll();
        return list;
    }

    public List<PaymentTypeConfig> findPaymentTypeConfigById(Long id) {
        PaymentType type = paymentTypeDao.getOne(id);
        if(type!=null){
            List<PaymentTypeConfig> list = JSON.parseArray(type.getConfigJson(),PaymentTypeConfig.class);
            for (PaymentTypeConfig config : list) {
                config.setShowText(msService.getMessage(config.getShowText()));
                config.setPlaceholder(msService.getMessage(config.getPlaceholder()));
            }
            return list;
        }
        return null;
    }

    public static void main(String[] args) {
        List<PaymentTypeConfig> list = new ArrayList<>();
        PaymentTypeConfig config = new PaymentTypeConfig();
        config.setType("input");
        config.setShowText("bankTransfer_name");
        config.setRequire(false);
        config.setFieldName("field_1");
        config.setPlaceholder("bankTransfer_name_placeholder");
        list.add(config);
        PaymentTypeConfig config2 = new PaymentTypeConfig();
        config2.setType("input");
        config2.setShowText("bankTransfer_id");
        config2.setRequire(true);
        config2.setFieldName("field_2");
        config2.setPlaceholder("bankTransfer_id_placeholder");
        list.add(config2);
        PaymentTypeConfig config3 = new PaymentTypeConfig();
        config3.setType("input");
        config3.setShowText("bankTransfer_iban");
        config3.setRequire(false);
        config3.setFieldName("field_3");
        config3.setPlaceholder("bankTransfer_iban_placeholder");
        list.add(config3);

        PaymentTypeConfig config4 = new PaymentTypeConfig();
        config4.setType("tip");
        config4.setShowText("absolutBank_tip");
        config4.setRequire(false);
        config4.setFieldName("field_4");
        config4.setPlaceholder("absolutBank_tip_placeholder");
        list.add(config4);

        System.out.println(JSON.toJSONString(list));

    }

    public PaymentType findPaymentTypeById(Long id) {
        return paymentTypeDao.findOne(id);
    }
}
