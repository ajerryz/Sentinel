package com.demo.sentinel.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

import javax.annotation.Resource;

@Service
public class HelloService {

    @Resource
    private OrderService orderService;

    static {
        initFlowRules();
    }

    /**
     * 自身调自己不会触发
     */
    //@Scheduled(fixedRate = 100)
    public void startTask() {
        hello("zs");
    }


    @SentinelResource(value = "hello-sentinel-annotation", fallback = "helloFallback")
    public void hello(String name) {
        System.out.println("hello " + name);
        System.out.println(orderService.getOrderById(1L));
    }

    public void helloFallback(String name) {
        System.out.println("=====> helloFallback " + name);
    }

    private static void initFlowRules() {
        List<FlowRule> flowRules = new ArrayList<>();
        FlowRule flowRule = new FlowRule();
        flowRule.setResource("hello-sentinel-annotation");
        flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS); // qps=20
        flowRule.setCount(1);
        flowRules.add(flowRule);
        FlowRuleManager.loadRules(flowRules);
    }
}
