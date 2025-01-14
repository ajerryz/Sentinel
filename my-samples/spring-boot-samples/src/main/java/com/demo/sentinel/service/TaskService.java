package com.demo.sentinel.service;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskService {
    @Resource
    private HelloService helloService;

    // @Scheduled(fixedRate = 100)
    public void startTask() {
        helloService.hello("zs");
    }
}
