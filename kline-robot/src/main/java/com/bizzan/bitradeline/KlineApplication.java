package com.bizzan.bitradeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 */
@EnableScheduling
@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
public class KlineApplication {
    public static void main( String[] args ){
    	SpringApplication.run(KlineApplication.class,args);
    }
}
