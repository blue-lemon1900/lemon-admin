package org.lemon.commons.idempotent.config;

import org.lemon.commons.idempotent.aspectj.RepeatSubmitAspect;
import org.lemon.commons.security.config.SecurityBeanConfig;
import org.lemon.commons.security.config.properties.LemonSecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConfiguration;

/**
 * 幂等功能配置
 *
 * @author Lion Li
 */
@AutoConfiguration(after = {RedisConfiguration.class, SecurityBeanConfig.class})
public class IdempotentConfig {

    @Bean
    public RepeatSubmitAspect repeatSubmitAspect(LemonSecurityProperties lemonSecurityProperties) {
        return new RepeatSubmitAspect(lemonSecurityProperties);
    }
}
