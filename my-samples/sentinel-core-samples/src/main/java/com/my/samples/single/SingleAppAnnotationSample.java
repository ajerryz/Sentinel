package com.my.samples.single;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

public class SingleAppAnnotationSample {

    private static final String RESOURCE_NAME = "hello-sentinel-annotation";

    public static void main(String[] args) {
        initFlowRules();

        SingleAppAnnotationSample obj = new SingleAppAnnotationSample();
        while (true) {
            obj.hello("zs");
        }
    }

    @SentinelResource(value = RESOURCE_NAME, fallback = "helloFallback")
    public void hello(String name) {
        System.out.println("hello," + name);
    }

    public void helloFallback(String name) {
        System.out.println("=====> fallback: " + name);
    }

    private static void initFlowRules() {
        List<FlowRule> flowRules = new ArrayList<>();
        FlowRule flowRule = new FlowRule();
        flowRule.setResource(RESOURCE_NAME);
        flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS); // qps=20
        flowRule.setCount(50);
        flowRules.add(flowRule);
        FlowRuleManager.loadRules(flowRules);
    }
}
