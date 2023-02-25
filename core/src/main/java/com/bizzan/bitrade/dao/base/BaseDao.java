package com.bizzan.bitrade.dao.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @description
 * @date 2019/1/18 10:38
 */
@NoRepositoryBean
public interface BaseDao<T> extends JpaRepository<T,Long>,JpaSpecificationExecutor<T>,QueryDslPredicateExecutor<T> {
}
