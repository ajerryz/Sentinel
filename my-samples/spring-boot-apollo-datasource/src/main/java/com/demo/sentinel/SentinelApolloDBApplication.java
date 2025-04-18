package com.demo.sentinel;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author jerry zhang
 * @since 2025/4/19 02:10
 */
@SpringBootApplication
@EnableApolloConfig
@EnableScheduling
public class SentinelApolloDBApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentinelApolloDBApplication.class, args);
    }
}
