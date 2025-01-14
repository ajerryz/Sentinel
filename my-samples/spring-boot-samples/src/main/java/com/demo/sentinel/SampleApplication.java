package com.demo.sentinel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;

@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication
public class SampleApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SampleApplication.class);

        SentinelResourceAspect bean = context.getBean(SentinelResourceAspect.class);
        System.out.println(bean);
    }

}
