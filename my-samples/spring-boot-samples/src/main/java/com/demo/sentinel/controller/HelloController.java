package com.demo.sentinel.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.sentinel.service.HelloService;

@RestController
public class HelloController {

    @Resource
    private HelloService helloService;

    @RequestMapping("/hello-sentinel")
    public Object helloSentinel() {
        // sentinel-core 直接支持的功能。
        helloService.hello("zs");
        return "OK";
    }
}
