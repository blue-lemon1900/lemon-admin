package org.lemon.commons.tenant.config;

import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.lemon.commons.core.config.properties.CaptchaProperties;
import org.lemon.commons.tenant.config.properties.TenantProperties;
import org.lemon.commons.redis.config.RedisConfig;
import org.lemon.commons.redis.config.properties.RedissonProperties;
import org.lemon.commons.tenant.handle.PlusTenantLineHandler;
import org.lemon.commons.tenant.handle.TenantKeyPrefixHandler;
import org.lemon.commons.tenant.manager.TenantSpringCacheManager;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 租户配置类
 *
 * @author Lion Li
 */
@AutoConfiguration(after = {RedisConfig.class})
@ConditionalOnProperty(value = "tenant.enable", havingValue = "true")
@EnableConfigurationProperties(TenantProperties.class)
public class TenantConfig {

    @ConditionalOnClass(TenantLineInnerInterceptor.class)
    @AutoConfiguration
    static class MybatisPlusConfiguration {

        /**
         * 多租户插件
         */
        @Bean
        public TenantLineInnerInterceptor tenantLineInnerInterceptor(TenantProperties tenantProperties) {
            return new TenantLineInnerInterceptor(new PlusTenantLineHandler(tenantProperties));
        }
    }

    @Bean
    public RedissonAutoConfigurationCustomizer tenantRedissonCustomizer(RedissonProperties redissonProperties) {
        return config -> {
            TenantKeyPrefixHandler nameMapper = new TenantKeyPrefixHandler(redissonProperties.getKeyPrefix());
            // 设置多租户 redis key前缀（Redisson 4.x 在顶层 Config 上设置）
            config.setNameMapper(nameMapper);
        };
    }

    /**
     * 多租户缓存管理器
     */
    @Primary
    @Bean
    public CacheManager tenantCacheManager() {
        return new TenantSpringCacheManager();
    }
}
