# DataSource继承关系
```text
ReadableDatasource
T loadConfig(),
S readSource(),
SentinelProperty<T> getProperty(),
void close()
        |
        |
AbstractDataSource(Converter,SentinelProperty)
T loadConfig()实现
T loadConfig(S conf)实现
SentinelProperty<T> getProperty() 实现
        |
        |
        |
        |
        |
ApolloDataSource/Push模式
```
