package com.bizzan.bitrade.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date: create in 16:47 2019/7/2
 * @Modified:
 */
@Configuration
@Component
public class JDBCConfig {

   
    @Value("${spring.datasource.url}")
    private String dbURRL;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;


    public String getDbURRL() {
        return dbURRL;
    }

    public void setDbURRL(String dbURRL) {
        this.dbURRL = dbURRL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
