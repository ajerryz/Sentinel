package com.demo.sentinel.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jerry zhang
 * @since 2025/4/19 02:35
 */
@Configuration
public class ApolloConfig {

    //@Bean
    public Config apolloConfig() {
        return ConfigService.getAppConfig();
    }
}
