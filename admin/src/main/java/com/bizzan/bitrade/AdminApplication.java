package com.bizzan.bitrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 */
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy=true)
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}