package org.lemon.commons.redis.config;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lemon.commons.core.utils.spring.SpringUtils;
import org.lemon.commons.redis.config.properties.RedissonProperties;
import org.lemon.commons.redis.handler.KeyPrefixHandler;
import org.lemon.commons.redis.handler.RedisExceptionHandler;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.CompositeCodec;
import org.redisson.codec.TypedJsonJackson3Codec;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.module.SimpleModule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * redis配置
 *
 * @author Lion Li
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(RedissonProperties.class)
@RequiredArgsConstructor
public class RedisConfig {

    private final RedissonProperties redissonProperties;

    @Bean
    public RedissonAutoConfigurationCustomizer redissonCustomizer() {
        return config -> {
            // Jackson 3：JavaTimeModule 已内置，只需注册自定义格式的序列化器
            SimpleModule timeModule = new SimpleModule();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
            timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));

            // Jackson 3：使用 JsonMapper.builder() 构建器模式，替代 Jackson 2 的 setter 方法
            ObjectMapper om = JsonMapper.builder()
                    .addModule(timeModule)
                    .defaultTimeZone(TimeZone.getDefault())
                    // 替代 om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
                    .changeDefaultVisibility(v -> v.withVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY))
                    // 替代 om.activateDefaultTyping(..., NON_FINAL)：序列化时携带全类名，支持多态反序列化
                    // 允许所有类型参与多态反序列化（等价于旧版 LaissezFaireSubTypeValidator）
                    .activateDefaultTyping(BasicPolymorphicTypeValidator.builder().allowIfSubType(Object.class).build(), DefaultTyping.NON_FINAL)
                    .build();

            TypedJsonJackson3Codec jsonCodec = new TypedJsonJackson3Codec(Object.class, om);
            // 组合序列化：key 使用 String，value 使用 JSON（携带类型信息）
            CompositeCodec codec = new CompositeCodec(StringCodec.INSTANCE, jsonCodec, jsonCodec);

            config.setThreads(redissonProperties.getThreads())
                    .setNettyThreads(redissonProperties.getNettyThreads())
                    .setUseScriptCache(true)
                    .setCodec(codec)
                    // 设置redis key前缀
                    .setNameMapper(new KeyPrefixHandler(redissonProperties.getKeyPrefix()));

            // 判断是否启用虚拟线程
            if (SpringUtils.isVirtual()) {
                config.setNettyExecutor(new VirtualThreadTaskExecutor("redisson-"));
            }

            RedissonProperties.SingleServerConfig singleServerConfig = redissonProperties.getSingleServerConfig();
            if (ObjectUtil.isNotNull(singleServerConfig)) {
                config.useSingleServer()
                        .setTimeout(singleServerConfig.getTimeout())
                        .setClientName(singleServerConfig.getClientName())
                        .setIdleConnectionTimeout(singleServerConfig.getIdleConnectionTimeout())
                        .setSubscriptionConnectionPoolSize(singleServerConfig.getSubscriptionConnectionPoolSize())
                        .setConnectionMinimumIdleSize(singleServerConfig.getConnectionMinimumIdleSize())
                        .setConnectionPoolSize(singleServerConfig.getConnectionPoolSize());
            }

            RedissonProperties.ClusterServersConfig clusterServersConfig = redissonProperties.getClusterServersConfig();
            if (ObjectUtil.isNotNull(clusterServersConfig)) {
                config.useClusterServers()
                        .setTimeout(clusterServersConfig.getTimeout())
                        .setClientName(clusterServersConfig.getClientName())
                        .setIdleConnectionTimeout(clusterServersConfig.getIdleConnectionTimeout())
                        .setSubscriptionConnectionPoolSize(clusterServersConfig.getSubscriptionConnectionPoolSize())
                        .setMasterConnectionMinimumIdleSize(clusterServersConfig.getMasterConnectionMinimumIdleSize())
                        .setMasterConnectionPoolSize(clusterServersConfig.getMasterConnectionPoolSize())
                        .setSlaveConnectionMinimumIdleSize(clusterServersConfig.getSlaveConnectionMinimumIdleSize())
                        .setSlaveConnectionPoolSize(clusterServersConfig.getSlaveConnectionPoolSize())
                        .setReadMode(clusterServersConfig.getReadMode())
                        .setSubscriptionMode(clusterServersConfig.getSubscriptionMode());
            }

            log.info("初始化 redis 配置");
        };
    }

    /**
     * 异常处理器
     */
    @Bean
    public RedisExceptionHandler redisExceptionHandler() {
        return new RedisExceptionHandler();
    }
}
