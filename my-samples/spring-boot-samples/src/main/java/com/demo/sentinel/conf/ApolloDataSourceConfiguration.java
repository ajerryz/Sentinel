package com.demo.sentinel.conf;

import com.alibaba.csp.sentinel.datasource.apollo.ApolloDataSource;
import com.alibaba.csp.sentinel.slots.block.Rule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author jerry zhang
 * @since 2025/1/15 01:43
 */
@Configuration
public class ApolloDataSourceConfiguration {

    @Bean
    public ApolloDataSource<List<FlowRule>> apolloFlowRuleDataSource() {
        String appId = "tsentinel";
        String apolloMetaServerAddr = "http://192.168.10.211:18080";
        System.setProperty("apollo.id", appId);
        System.setProperty("apollo.meta", apolloMetaServerAddr);
        System.setProperty("env","dev");

        String namespace = "application";
        String flowRuleKey = "sentinel-flowkeys";
        String defaultFlowRuleValue = "[]";
        ApolloDataSource<List<FlowRule>> apolloFlowRuleDataSource = new ApolloDataSource<>(
                namespace,
                flowRuleKey,
                defaultFlowRuleValue,
                source -> {
                    List<FlowRule> flowRules = JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
                    });
                    return flowRules;
                });
        FlowRuleManager.register2Property(apolloFlowRuleDataSource.getProperty());
        return apolloFlowRuleDataSource;
    }
}
