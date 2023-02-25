package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

/**
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年02月10日
 */
public interface CountryDao extends JpaRepository<Country,String>,JpaSpecificationExecutor<Country>,QueryDslPredicateExecutor<Country> {
    @Query("select a from Country a order by a.sort")
    List<Country> findAllOrderBySort();

    @Query("select a from Country a where a.appSupport=1 order by a.sort")
    List<Country> findAllAppSupport();

    Country findByZhName(String zhname);

    Country findByEnName(String enName);

}
