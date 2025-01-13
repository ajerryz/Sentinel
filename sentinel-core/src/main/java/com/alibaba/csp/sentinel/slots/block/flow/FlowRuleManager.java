/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.slots.block.flow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.alibaba.csp.sentinel.concurrent.NamedThreadFactory;
import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.csp.sentinel.node.metric.MetricTimerListener;
import com.alibaba.csp.sentinel.property.DynamicSentinelProperty;
import com.alibaba.csp.sentinel.property.PropertyListener;
import com.alibaba.csp.sentinel.property.SentinelProperty;

/**
 * <p>
 * 一个资源可以拥有多条规则，规则生效顺序如下：:
 * <ol>
 * <li>requests from specified caller(请求来自指定的调用者)</li>
 * <li>no specified caller(未指定调用者)</li>
 * </ol>
 * </p>
 *
 * @author jialiang.linjl
 * @author Eric Zhao
 * @author Weihua
 */
public class FlowRuleManager {


    // 存放流控规则(修改原子性)
    private static final AtomicReference<Map<String, List<FlowRule>>> flowRules = new AtomicReference<Map<String, List<FlowRule>>>();

    // 监听器
    private static final FlowPropertyListener LISTENER = new FlowPropertyListener();

    // 当前配置
    private static SentinelProperty<List<FlowRule>> currentProperty = new DynamicSentinelProperty<List<FlowRule>>();

    // 调度器线程-1个线程。
    @SuppressWarnings("PMD.ThreadPoolCreationRule")
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1,
        new NamedThreadFactory("sentinel-metrics-record-task", true));

    // 使用该类时才初始化
    static {
        // 初始空流控规则
        flowRules.set(Collections.<String, List<FlowRule>>emptyMap());
        // 将流控监听器
        currentProperty.addListener(LISTENER);
        // 启动 指标定时 调度器
        startMetricTimerListener();
    }

    /**
     * <p> Start the MetricTimerListener
     * <ol>
     *     <li>If the flushInterval more than 0,
     * the timer will run with the flushInterval as the rate </li>.
     *      <li>If the flushInterval less than 0(include) or value is not valid,
     * then means the timer will not be started </li>
     * <ol></p>
     */
    private static void startMetricTimerListener() {
        long flushInterval = SentinelConfig.metricLogFlushIntervalSec();
        if (flushInterval <= 0) {
            RecordLog.info("[FlowRuleManager] The MetricTimerListener is'n started. If you want to start it, "
                    + "please change the value(current: {}) of config({}) more than 0 to start it.", flushInterval,
                    SentinelConfig.METRIC_FLUSH_INTERVAL);
            return;
        }
        // 固定s 运行调度器
        SCHEDULER.scheduleAtFixedRate(new MetricTimerListener(), 0, flushInterval, TimeUnit.SECONDS);
    }

    /**
     * Listen to the {@link SentinelProperty} for {@link FlowRule}s. The property is the source of {@link FlowRule}s.
     * Flow rules can also be set by {@link #loadRules(List)} directly.
     *
     * @param property the property to listen.
     */
    public static void register2Property(SentinelProperty<List<FlowRule>> property) {
        AssertUtil.notNull(property, "property cannot be null");
        synchronized (LISTENER) {
            RecordLog.info("[FlowRuleManager] Registering new property to flow rule manager");
            currentProperty.removeListener(LISTENER);
            property.addListener(LISTENER);
            currentProperty = property;
        }
    }

    /**
     * Get a copy of the rules.
     *
     * @return a new copy of the rules.
     */
    public static List<FlowRule> getRules() {
        List<FlowRule> rules = new ArrayList<FlowRule>();
        for (Map.Entry<String, List<FlowRule>> entry : flowRules.get().entrySet()) {
            rules.addAll(entry.getValue());
        }
        return rules;
    }

    /**
     * Load {@link FlowRule}s, former rules will be replaced.
     *
     * @param rules new rules to load.
     */
    public static void loadRules(List<FlowRule> rules) {
        currentProperty.updateValue(rules);
    }

    static Map<String, List<FlowRule>> getFlowRuleMap() {
        return flowRules.get();
    }

    public static boolean hasConfig(String resource) {
        return flowRules.get().containsKey(resource);
    }

    public static boolean isOtherOrigin(String origin, String resourceName) {
        if (StringUtil.isEmpty(origin)) {
            return false;
        }

        List<FlowRule> rules = flowRules.get().get(resourceName);

        if (rules != null) {
            for (FlowRule rule : rules) {
                if (origin.equals(rule.getLimitApp())) {
                    return false;
                }
            }
        }

        return true;
    }

    private static final class FlowPropertyListener implements PropertyListener<List<FlowRule>> {

        @Override
        public void configUpdate(List<FlowRule> value) {
            Map<String, List<FlowRule>> rules = FlowRuleUtil.buildFlowRuleMap(value);
            //the rules was always not null, it's no need to check nullable
            //remove checking to avoid IDE warning
            flowRules.set(rules);
            RecordLog.info("[FlowRuleManager] Flow rules received: {}", rules);
        }

        @Override
        public void configLoad(List<FlowRule> conf) {
            Map<String, List<FlowRule>> rules = FlowRuleUtil.buildFlowRuleMap(conf);
            flowRules.set(rules);
            RecordLog.info("[FlowRuleManager] Flow rules loaded: {}", rules);
        }
    }

}
