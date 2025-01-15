package com.demo.sentinel.service;

import org.springframework.stereotype.Service;

/**
 * @author jerry zhang
 * @since 2025/1/14 23:31
 */
@Service
public class OrderService {


    public String getOrderById(Long id) {
        return "orderId:" + id;
    }
}
