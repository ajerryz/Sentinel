package com.my.samples.single;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author jerry zhang
 * @since 2025/1/14 02:11
 */
public class SingleAppSample {

    private static final String RESOURCE_NAME = "hello-sentinel";

    public static void main(String[] args) {
        initFlowRules();

        while (true) {
            Entry entry = null;
            try {
                entry = SphU.entry(RESOURCE_NAME);
                System.out.println("业务代码");
            } catch (BlockException e) {
                // 处理被限流逻辑
                System.out.println("=========> blocked");
            } finally {
                if (entry != null) {
                    entry.exit();
                }
            }
        }

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
