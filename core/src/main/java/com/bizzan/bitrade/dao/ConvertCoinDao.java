package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.entity.ConvertCoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


/***
 *
 */
@Repository
public interface ConvertCoinDao extends JpaRepository<ConvertCoin, String>, JpaSpecificationExecutor<ConvertCoin>, QueryDslPredicateExecutor<ConvertCoin> {

    ConvertCoin findByCoinUnit(String coinUnit);

    List<ConvertCoin> findByStatus(Integer status);
}
