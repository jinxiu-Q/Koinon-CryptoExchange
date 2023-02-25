package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.entity.ConvertOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;


/***
 *
 */
@Repository
public interface ConvertOrderDao extends JpaRepository<ConvertOrder, String>, JpaSpecificationExecutor<ConvertOrder>, QueryDslPredicateExecutor<ConvertOrder> {

}
