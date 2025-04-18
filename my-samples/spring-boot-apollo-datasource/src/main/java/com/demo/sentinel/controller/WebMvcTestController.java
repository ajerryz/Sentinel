package com.demo.sentinel.controller;

import com.demo.sentinel.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.Map;

/**
 * @author jerry zhang
 * @since 2025/4/19 02:18
 */
@Controller
public class WebMvcTestController {

    static final Logger LOG = LoggerFactory.getLogger(WebMvcTestController.class);


    @Value("${config.name:'default'}")
    public String configName;


    @Autowired
    TestService testService;

    @GetMapping("/demo")
    @ResponseBody
    public Object demo() {
        Map<String, Object> map = testService.doBiz("100");
        map.put("result", "success");
        map.put("configName", configName);
        return map;
    }


    @Scheduled(fixedRate = 3000)
    public void print() {
        LOG.info(configName);
    }
}
