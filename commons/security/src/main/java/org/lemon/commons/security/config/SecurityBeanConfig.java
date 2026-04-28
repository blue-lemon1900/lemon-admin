package org.lemon.commons.security.config;

import org.lemon.commons.security.config.properties.SecurityProperties;
import org.lemon.commons.security.filter.GlobalExceptionFilter;
import org.lemon.commons.security.handler.LoginFailHandler;
import org.lemon.commons.security.handler.LoginSuccessHandler;
import org.lemon.commons.security.handler.exception.AccessDeniedHandlerImpl;
import org.lemon.commons.security.handler.exception.AuthenticationEntryPointImpl;
import org.lemon.commons.security.login.username.UsernameAuthenticationProvider;
import org.lemon.commons.security.service.UsernameService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.HashMap;
import java.util.Map;

@AutoConfiguration
@AutoConfigureOrder(-1)
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityBeanConfig {

    /**
     * 密码加密使用的编码器。
     * <p>
     * 采用 Spring Security 7 推荐的 {@link DelegatingPasswordEncoder}：
     * <ul>
     *   <li>新密码用 {@code lemon.security.password-encoder-id} 配置的算法编码（默认 argon2），
     *       存储格式为 {@code {argon2}...}；切换 yml 配置即可迁移加密算法</li>
     *   <li>历史 {@code {bcrypt}} / {@code {pbkdf2}} / {@code {scrypt}} 前缀仍可正确校验，
     *       便于平滑迁移；登录时由 {@code UsernameAuthenticationProvider}
     *       通过 {@link PasswordEncoder#upgradeEncoding(String)} 触发自动升级到当前 idForEncode。</li>
     *   <li>{@code setDefaultPasswordEncoderForMatches}：兼容数据库中"无 {id} 前缀"的纯
     *       {@code $2a$..} 老 bcrypt 串，避免直接抛 IllegalArgumentException。</li>
     * </ul>
     * Argon2 实现依赖 BouncyCastle(已通过传递依赖引入)。
     */
    @Bean
    public PasswordEncoder passwordEncoder(SecurityProperties properties) {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("argon2", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("pbkdf2", Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoders.put("scrypt", SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8());

        DelegatingPasswordEncoder delegating = new DelegatingPasswordEncoder(properties.getPasswordEncoderId(), encoders);
        delegating.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
        return delegating;
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
                                                                         @Value("${lemon.tenant.enable:false}") boolean tenantEnable) {
        return new UsernameAuthenticationProvider(usernameService, passwordEncoder, tenantEnable);
    }

    /**
     * 阻止 Spring Boot 将 GlobalSpringSecurityExceptionFilter 自动注册到 Servlet 容器，
     * 由 HttpSecurity 手动将其加入过滤器链，避免重复执行。
     * <p>
     * 注入 MVC 的 {@code handlerExceptionResolver}，让 Filter 链中的非安全异常委托给
     * {@code @RestControllerAdvice}（{@code GlobalExceptionHandler}）统一处理。
     */
    @Bean
    public FilterRegistrationBean<GlobalExceptionFilter> tenantFilterRegistration(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
        GlobalExceptionFilter filter = new GlobalExceptionFilter(handlerExceptionResolver);
        FilterRegistrationBean<GlobalExceptionFilter> registration = new FilterRegistrationBean<>(filter);

        // 并将其 enabled 属性设置为 false 来告诉 Spring Boot 不要将其注册到容器中
        registration.setEnabled(false);
        return registration;
    }
}
