package com.demo.sentinel.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author jerry zhang
 * @since 2025/4/19 02:55
 */
@Service
public class TestService {


    @SentinelResource
    public Map<String, Object> doBiz(String id) {

        double r = Math.random();

        try {
            TimeUnit.SECONDS.sleep((long) (r * 5));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "zs");
        return map;
    }
}
