/*
 * Copyright 1999-2020 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.log;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import com.alibaba.csp.sentinel.util.StringUtil;

/**
 * SPI provider of Sentinel {@link Logger}.
 *
 * @author Eric Zhao
 * @since 1.7.2
 */
public final class LoggerSpiProvider {

    private static final Map<String, Logger> LOGGER_MAP = new HashMap<>();

    // 使用SPI方式获取Sentinel自身的日志类
    static {
        // NOTE: this class SHOULD NOT depend on any other Sentinel classes
        // except the util classes to avoid circular dependency.
        // 翻译：
        // 此类不应依赖于除实用程序类之外的任何其他 Sentinel 类，以避免循环依赖。
        try {
            resolveLoggers();
        } catch (Throwable t) {
            System.err.println("Failed to resolve Sentinel Logger SPI");
            t.printStackTrace();
        }
    }

    /**
     * 静态方法，获取Log
     */
    public static Logger getLogger(String name) {
        if (name == null) {
            return null;
        }
        return LOGGER_MAP.get(name);
    }

    /**
     * SPI 解析Log, SPI的 interface 是该包下的Logger <br/>
     * 1.日志必须使用LogTarget注解，该注解的value default="sentinelRecordLogger"，用作Map的key,以便getLogger(String)获取
     */
    private static void resolveLoggers() {
        // NOTE: Here we cannot use {@code SpiLoader} directly because it depends on the RecordLog.
        ServiceLoader<Logger> loggerLoader = ServiceLoader.load(Logger.class);

        for (Logger logger : loggerLoader) {
            LogTarget annotation = logger.getClass().getAnnotation(LogTarget.class);
            if (annotation == null) {
                continue;
            }
            String name = annotation.value();
            // Load first encountered logger if multiple loggers are associated with the same name.
            if (StringUtil.isNotBlank(name) && !LOGGER_MAP.containsKey(name)) {
                LOGGER_MAP.put(name, logger);
                System.out.println("Sentinel Logger SPI loaded for <" + name + ">: "
                        + logger.getClass().getCanonicalName());
            }
        }
    }

    private LoggerSpiProvider() {
    }
}
