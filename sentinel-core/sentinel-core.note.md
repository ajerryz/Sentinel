# Sentinel-core 模块笔记
- com.alibaba.csp.sentinel.init SPI方式加载`InitFunc`并按照`@InitOrder`顺序执行对应的`init`方法
- com.alibaba.csp.sentinel.annotation `@SentinelResource`注解
- com.alibaba.csp.sentinel.config 初始化本地sentinel的常规配置到`SentinelConfig`的props这个并发安全的Set中
- com.alibaba.csp.sentinel.context 每个 `SphU#entry()` 或者 `SphO#entry()` 都应该在一个 `Context`, 如果没有明确调用 `ContextUtil#enter()` , 将使用 DEFAULT Context
- com.alibaba.csp.sentinel.concurrent 方便使用的一个`ThreadFactory`
- com.alibaba.csp.sentinel.log sentinel内部使用的日志及适配