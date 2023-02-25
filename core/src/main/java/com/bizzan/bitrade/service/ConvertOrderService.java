package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.ConvertOrderDao;
import com.bizzan.bitrade.entity.ConvertOrder;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


/**
 * @author Hevin E-Mali:390330302@qq.com
 * @description
 * @date 2021/12/29 14:50
 */
@Service
public class ConvertOrderService extends BaseService {
    @Autowired
    private ConvertOrderDao convertOrderDao;

    public Page<ConvertOrder> findAll(Predicate predicate, Pageable pageable) {
        return convertOrderDao.findAll(predicate,pageable);
    }
    public ConvertOrder save(ConvertOrder order) {
        return convertOrderDao.save(order);
    }

    public Page<ConvertOrder> queryByMember(Long memberId, int pageNo, int pageSize) {
        Sort orders = Criteria.sortStatic("createTime.desc");
        //分页参数
        PageRequest pageRequest = new PageRequest(pageNo, pageSize, orders);
        //查询条件
        Criteria<ConvertOrder> specification = new Criteria<ConvertOrder>();
        specification.add(Restrictions.eq("memberId", memberId, false));
        return convertOrderDao.findAll(specification, pageRequest);
    }
}
