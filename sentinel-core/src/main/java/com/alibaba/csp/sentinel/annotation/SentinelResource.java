/*
 * Copyright 1999-2020 Alibaba Group Holding Ltd.
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
package com.alibaba.csp.sentinel.annotation;

import com.alibaba.csp.sentinel.EntryType;

import java.lang.annotation.*;

/**
 * 该注解表明了Sentinel资源的定义
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SentinelResource {

    /**
     * Sentinel 资源的名称
     */
    String value() default "";

    /**
     * 入口类型（入站或出站），默认为出站
     */
    EntryType entryType() default EntryType.OUT;

    /**
     * 资源的分类（类型）
     * @since 1.7.0
     */
    int resourceType() default 0;

    /**
     * 块异常函数的名称，默认为空
     */
    String blockHandler() default "";

    /**
     * blockHandler 默认与原方法位于同一个类中，但如果有多个方法具有相同的签名，
     * 且想要设置相同的 block handler，则用户可以设置 block handler 所在的类，注意 block handler 方法必须是静态的。<br/><br/>
     * 块处理程序所在的类，不应提供多个类
     */
    Class<?>[] blockHandlerClass() default {};

    /**
     * fallback函数的名称，默认为空
     */
    String fallback() default "";

    /**
     * defaultFallback 作为默认的通用 fallback 方法，不应接受任何参数，且返回类型应与原始方法兼容。
     *
     * @return name of the default fallback method, empty by default
     * @since 1.6.0
     */
    String defaultFallback() default "";

    /**
     * fallback 默认与原方法位于同一个类中，但如果有多个方法共享相同的签名，并希望设置相同的 fallback，
     * 则用户可以设置 fallback 函数所在的类。注意，共享的 fallback 方法必须是静态的。
     *
     * @return the class where the fallback method is located (only single class)
     * @since 1.6.0
     */
    Class<?>[] fallbackClass() default {};

    /**
     * 要跟踪的异常类列表，默认情况下为 Throwable
     * @since 1.5.1
     */
    Class<? extends Throwable>[] exceptionsToTrace() default {Throwable.class};
    
    /**
     * 表示要忽略的异常。请注意，exceptionsToTrace 不应与 exceptionsToIgnore 同时出现，否则 exceptionsToIgnore 的优先级会更高。
     *
     * @return the list of exception classes to ignore, empty by default
     * @since 1.6.0
     */
    Class<? extends Throwable>[] exceptionsToIgnore() default {};
}
