package com.alibaba.csp.sentinel.datasource.apollo;

import com.alibaba.csp.sentinel.datasource.AbstractDataSource;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.log.RecordLog;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A read-only {@code DataSource} with <a href="http://github.com/ctripcorp/apollo">Apollo</a> as its configuration
 * source.
 * <br />
 * When the rule is changed in Apollo, it will take effect in real time.
 *
 * @author Jason Song
 * @author Haojun Ren
 */
public class ApolloDataSource<T> extends AbstractDataSource<String, T> {

    private static final Logger log = LoggerFactory.getLogger(ApolloDataSource.class);
    // apollo config
    private final Config config;
    private final String ruleKey;
    private final String defaultRuleValue;

    // apollo ConfigChangeListener
    private ConfigChangeListener configChangeListener;

    /**
     * Constructs the Apollo data source
     *
     * @param namespaceName        Apollo 的命名空间，不能为null或者empty
     * @param ruleKey              命名空间中的key,也就是存储规则的key,不能为null 或 empty
     * @param defaultRuleValue     规则默认值，当有任何错误发生时
     * @param parser               将字符串解析为实际流控规则的解析器。
     */
    public ApolloDataSource(String namespaceName, String ruleKey, String defaultRuleValue,
                            Converter<String, T> parser) {
        // 父抽象类，存放解析器和创建SentinelProperty<T>
        super(parser);

        Preconditions.checkArgument(!Strings.isNullOrEmpty(namespaceName), "Namespace name could not be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(ruleKey), "RuleKey could not be null or empty!");

        this.ruleKey = ruleKey;
        this.defaultRuleValue = defaultRuleValue;

        // Apollo Client 获取配置
        this.config = ConfigService.getConfig(namespaceName);

        // 初始化：
        // 1.初始化Apollo监听器
        initialize();

        RecordLog.info("Initialized rule for namespace: {}, rule key: {}", namespaceName, ruleKey);
    }

    private void initialize() {
        // 初始化Apollo监听器
        initializeConfigChangeListener();

        loadAndUpdateRules();
    }

    private void loadAndUpdateRules() {
        try {
            T newValue = loadConfig();
            if (newValue == null) {
                RecordLog.warn("[ApolloDataSource] WARN: rule config is null, you may have to check your data source");
            }
            // ====== zn 日志
            log.info("newValue: {}", newValue);
            // ====== zn
            getProperty().updateValue(newValue);
        } catch (Throwable ex) {
            RecordLog.warn("[ApolloDataSource] Error when loading rule config", ex);
        }
    }

    // 初始化Apollo配置监听器
    private void initializeConfigChangeListener() {
        configChangeListener = new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                ConfigChange change = changeEvent.getChange(ruleKey);
                //change is never null because the listener will only notify for this key

                // ===== zn 自定义日志
                log.info("oldValue={} newValue={}", change.getOldValue(), change.getNewValue());
                // ====== zn ======

                if (change != null) {
                    RecordLog.info("[ApolloDataSource] Received config changes: {}", change);
                }
                loadAndUpdateRules();
            }
        };
        config.addChangeListener(configChangeListener, Sets.newHashSet(ruleKey));
    }

    // readSource,从Apollo的本地缓存获取配置字符串
    @Override
    public String readSource() throws Exception {
        return config.getProperty(ruleKey, defaultRuleValue);
    }

    @Override
    public void close() throws Exception {
        config.removeChangeListener(configChangeListener);
    }
}
