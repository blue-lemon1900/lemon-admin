package org.lemon.commons.core.config;

import org.lemon.commons.core.config.properties.CaptchaProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 程序注解配置
 *
 * @author Lion Li
 */
@AutoConfiguration
@EnableAspectJAutoProxy
@EnableAsync(proxyTargetClass = true)
@EnableConfigurationProperties(CaptchaProperties.class)
public class ApplicationConfig {

}
