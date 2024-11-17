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
package com.alibaba.csp.sentinel.config;

import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.util.AppNameUtil;
import com.alibaba.csp.sentinel.util.ConfigUtil;
import com.alibaba.csp.sentinel.util.StringUtil;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.alibaba.csp.sentinel.util.ConfigUtil.addSeparator;

/**
 * 负责加载Sentinel公共配置的加载器。
 */
public final class SentinelConfigLoader {

    public static final String SENTINEL_CONFIG_ENV_KEY = "CSP_SENTINEL_CONFIG_FILE";
    public static final String SENTINEL_CONFIG_PROPERTY_KEY = "csp.sentinel.config.file";

    private static final String DEFAULT_SENTINEL_CONFIG_FILE = "classpath:sentinel.properties";

    // 存放Sentinel配置属性
    private static Properties properties = new Properties();

    // 加载配置
    static {
        try {
            load();
        } catch (Throwable t) {
            RecordLog.warn("[SentinelConfigLoader] Failed to initialize configuration items", t);
        }
    }

    /**
     * 执行加载配置
     */
    private static void load() {
        // Order: system property -> system env -> default file (classpath:sentinel.properties) -> legacy path
        // 从JVM Properties参数(csp.sentinel.config.file)获配置文件名
        String fileName = System.getProperty(SENTINEL_CONFIG_PROPERTY_KEY);
        if (StringUtil.isBlank(fileName)) {
            // 从环境变量(CSP_SENTINEL_CONFIG_FILE) 获取配置文件名
            fileName = System.getenv(SENTINEL_CONFIG_ENV_KEY);
            if (StringUtil.isBlank(fileName)) {
                // 获取classpath默认配置文件名(classpath:sentinel.properties)
                fileName = DEFAULT_SENTINEL_CONFIG_FILE;
            }
        }

        Properties p = ConfigUtil.loadProperties(fileName);
        if (p != null && !p.isEmpty()) {
            RecordLog.info("[SentinelConfigLoader] Loading Sentinel config from {}", fileName);
            properties.putAll(p);
        }

        for (Map.Entry<Object, Object> entry : new CopyOnWriteArraySet<>(System.getProperties().entrySet())) {
            String configKey = entry.getKey().toString();
            String newConfigValue = entry.getValue().toString();
            String oldConfigValue = properties.getProperty(configKey);
            properties.put(configKey, newConfigValue);
            if (oldConfigValue != null) {
                RecordLog.info("[SentinelConfigLoader] JVM parameter overrides {}: {} -> {}",
                        configKey, oldConfigValue, newConfigValue);
            }
        }
    }


    // 直接返回配置属性对象
    public static Properties getProperties() {
        return properties;
    }

}
