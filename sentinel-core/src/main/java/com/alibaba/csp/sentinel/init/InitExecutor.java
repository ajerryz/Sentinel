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
package com.alibaba.csp.sentinel.init;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.spi.SpiLoader;

/**
 * 1.加载已注册的InitFunc函数并按顺序执行。
 * 2.静态方法真正逻辑只会被执行一次
 * 3.静态doInit中执行每个排好序的InitFunc的init方法
 */
public final class InitExecutor {

    private static AtomicBoolean initialized = new AtomicBoolean(false);

    /**
     * 如果某个 InitFunc 抛出异常，init 进程会立即中断，应用程序退出。<br/>
     * 初始化只会执行一次。
     */
    public static void doInit() {
        // 该doInit()方法只会执行一次
        if (!initialized.compareAndSet(false, true)) {
            return;
        }
        try {
            // 1.SPI 加载 InitFunc实例
            // 加载或从缓存中获取通过Spi方式缓存的InitFunc实例
            List<InitFunc> initFuncs = SpiLoader.of(InitFunc.class).loadInstanceListSorted();
            List<OrderWrapper> initList = new ArrayList<OrderWrapper>();
            // 2.包装为带order
            for (InitFunc initFunc : initFuncs) {
                RecordLog.info("[InitExecutor] Found init func: {}", initFunc.getClass().getCanonicalName());
                // 如果InitFunc中使用的@InitOrder注解，则进行排序，并包装为OrderWrapper(int order,InitFunc func)
                insertSorted(initList, initFunc);
            }
            // 3.顺序执行InitFunc的具体内容
            for (OrderWrapper w : initList) {
                w.func.init();
                RecordLog.info("[InitExecutor] Executing {} with order {}",
                        w.func.getClass().getCanonicalName(), w.order);
            }
        } catch (Exception ex) {
            RecordLog.warn("[InitExecutor] WARN: Initialization failed", ex);
            ex.printStackTrace();
        } catch (Error error) {
            RecordLog.warn("[InitExecutor] ERROR: Initialization failed with fatal error", error);
            error.printStackTrace();
        }
    }

    private static void insertSorted(List<OrderWrapper> list, InitFunc func) {
        int order = resolveOrder(func);
        int idx = 0;
        for (; idx < list.size(); idx++) {
            if (list.get(idx).getOrder() > order) {
                break;
            }
        }
        list.add(idx, new OrderWrapper(order, func));
    }

    private static int resolveOrder(InitFunc func) {
        if (!func.getClass().isAnnotationPresent(InitOrder.class)) {
            return InitOrder.LOWEST_PRECEDENCE;
        } else {
            return func.getClass().getAnnotation(InitOrder.class).value();
        }
    }

    private InitExecutor() {
    }

    /**
     * 排序的InitFunc包装器
     */
    private static class OrderWrapper {
        private final int order;
        private final InitFunc func;

        OrderWrapper(int order, InitFunc func) {
            this.order = order;
            this.func = func;
        }

        int getOrder() {
            return order;
        }

        InitFunc getFunc() {
            return func;
        }
    }
}
