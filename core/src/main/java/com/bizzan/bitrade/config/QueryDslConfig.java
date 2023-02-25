package com.bizzan.bitrade.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

/**
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @description
 * @date 2019/1/18 11:29
 */
@Configuration
public class QueryDslConfig {
    @Bean
    public JPAQueryFactory getJPAQueryFactory(EntityManager entityManager){
        return new JPAQueryFactory(entityManager);
    }
}
