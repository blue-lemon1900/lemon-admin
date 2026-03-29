package org.lemon.commons.encrypt.config;

import jakarta.servlet.DispatcherType;
import org.lemon.commons.encrypt.filter.CryptoFilter;
import org.lemon.commons.encrypt.properties.ApiDecryptProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * api 解密自动配置
 *
 * @author wdhcr
 */
@AutoConfiguration
@EnableConfigurationProperties(ApiDecryptProperties.class)
@ConditionalOnProperty(value = "api-decrypt.enabled", havingValue = "true")
public class ApiDecryptAutoConfiguration {

    @Bean
    @FilterRegistration(
            name = "cryptoFilter",
            urlPatterns = "/*",
            order = FilterRegistrationBean.HIGHEST_PRECEDENCE,
            dispatcherTypes = DispatcherType.REQUEST
    )
    public CryptoFilter cryptoFilter(ApiDecryptProperties properties,
                                     @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver,
                                     RequestMappingHandlerMapping requestMappingHandlerMapping) {
        return new CryptoFilter(properties, handlerExceptionResolver, requestMappingHandlerMapping);
    }
}
