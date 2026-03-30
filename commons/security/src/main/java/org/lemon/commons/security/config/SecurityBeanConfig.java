package org.lemon.commons.security.config;

import org.lemon.commons.security.config.properties.LemonSecurityProperties;
import org.lemon.commons.security.filter.GlobalSpringSecurityExceptionFilter;
import org.lemon.commons.security.handler.LoginFailHandler;
import org.lemon.commons.security.handler.LoginSuccessHandler;
import org.lemon.commons.security.handler.exception.AccessDeniedHandlerImpl;
import org.lemon.commons.security.handler.exception.AuthenticationEntryPointImpl;
import org.lemon.commons.security.login.username.UsernameAuthenticationProvider;
import org.lemon.commons.security.service.SecurityFrameworkService;
import org.lemon.commons.security.service.UsernameService;
import org.lemon.commons.security.service.impl.SecurityFrameworkServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
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
     * 未认证（未登录）时的入口点处理器，返回 401 响应
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPointImpl();
    }

    /**
     * 已认证用户访问无权限资源时的处理器，返回 403 响应
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }

    /**
     * 异步任务上下文传播装饰器，自动将 SecurityContext 等上下文传播至 @Async 子线程。
     * Spring Boot 4 会将所有 TaskDecorator Bean 组合后应用到 AsyncTaskExecutor。
     */
    @Bean
    public TaskDecorator contextPropagatingTaskDecorator() {
        return new ContextPropagatingTaskDecorator();
    }

    /**
     * 登录认证失败处理器，认证不通过时触发
     */
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new LoginFailHandler();
    }

    /**
     * 登录认证成功处理器，认证通过后触发
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new LoginSuccessHandler();
    }

    /**
     * 账号密码登录认证
     *
     * @param usernameService 查询用户信息类
     * @param passwordEncoder 密码加密
     * @param tenantEnable    是否开启租户
     */
    @Bean
    public UsernameAuthenticationProvider usernameAuthenticationProvider(UsernameService usernameService,
                                                                         PasswordEncoder passwordEncoder,
                                                                         @Value("${tenant.enable:true}") boolean tenantEnable) {
        return new UsernameAuthenticationProvider(usernameService, passwordEncoder, tenantEnable);
    }

    /**
     * Security 工具服务，提供获取当前登录用户、权限判断等常用方法，Bean 名称 "ss" 方便在 SpEL 中引用
     */
    @Bean("ss")
    public SecurityFrameworkService securityFrameworkService() {
        return new SecurityFrameworkServiceImpl();
    }

    /**
     * 阻止 Spring Boot 将 GlobalSpringSecurityExceptionFilter 自动注册到 Servlet 容器，
     * 由 HttpSecurity 手动将其加入过滤器链，避免重复执行
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
