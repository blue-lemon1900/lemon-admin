package org.lemon.commons.security.config;

import org.lemon.commons.security.config.properties.LemonSecurityProperties;
import org.lemon.commons.security.context.TTLSecurityContextHolderStrategy;
import org.lemon.commons.security.filter.GlobalSpringSecurityExceptionFilter;
import org.lemon.commons.security.handler.LoginFailHandler;
import org.lemon.commons.security.handler.LoginSuccessHandler;
import org.lemon.commons.security.handler.exception.AccessDeniedHandlerImpl;
import org.lemon.commons.security.handler.exception.AuthenticationEntryPointImpl;
import org.lemon.commons.security.login.username.UsernameAuthenticationProvider;
import org.lemon.commons.security.service.SecurityFrameworkService;
import org.lemon.commons.security.service.UsernameService;
import org.lemon.commons.security.service.impl.SecurityFrameworkServiceImpl;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@AutoConfiguration
@AutoConfigureOrder(-1)
@EnableConfigurationProperties(LemonSecurityProperties.class)
public class SecurityBeanConfig {

    /**
     * 密码加密使用的编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * 认证失败处理类 Bean
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPointImpl();
    }

    /**
     * 权限不够处理器 Bean
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }

    /**
     * 声明调用 {@link SecurityContextHolder#setStrategyName(String)} 方法，
     * 设置使用 {@link TTLSecurityContextHolderStrategy} 作为 Security 的上下文策略
     */
    @Bean
    public MethodInvokingFactoryBean securityContextHolderMethodInvokingFactoryBean() {
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setTargetClass(SecurityContextHolder.class);
        methodInvokingFactoryBean.setTargetMethod("setStrategyName");
        methodInvokingFactoryBean.setArguments(TTLSecurityContextHolderStrategy.class.getName());
        return methodInvokingFactoryBean;
    }

    /**
     * 登录失败
     *
     * @return
     */
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new LoginFailHandler();
    }

    /**
     * 登录成功
     *
     * @return
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new LoginSuccessHandler();
    }

    /**
     * 帐号密码登录认证
     *
     * @param usernameService 查询用户信息类
     * @param passwordEncoder 密码加密
     * @return
     */
    @Bean
    public UsernameAuthenticationProvider usernameAuthenticationProvider(UsernameService usernameService, PasswordEncoder passwordEncoder) {
        return new UsernameAuthenticationProvider(usernameService, passwordEncoder);
    }

    /**
     * 使用 Spring Security 的缩写，方便使用
     *
     * @return
     */
    @Bean("ss")
    public SecurityFrameworkService securityFrameworkService() {
        return new SecurityFrameworkServiceImpl();
    }

    /**
     * 通过声明 FilterRegistrationBean bean,这使得 HttpSecurity 成为唯一添加它的
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean<GlobalSpringSecurityExceptionFilter> tenantFilterRegistration() {
        // 处理全局异常的 Filter
        GlobalSpringSecurityExceptionFilter filter = new GlobalSpringSecurityExceptionFilter();
        FilterRegistrationBean<GlobalSpringSecurityExceptionFilter> registration = new FilterRegistrationBean<>(filter);

        // 并将其 enabled 属性设置为 false 来告诉 Spring Boot 不要将其注册到容器中
        registration.setEnabled(false);
        return registration;
    }
}
