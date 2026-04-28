package org.lemon.commons.security.config;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lemon.commons.core.config.properties.CaptchaProperties;
import org.lemon.commons.security.authorization.AnonymousAccessAuthorizationManager;
import org.lemon.commons.security.config.properties.SecurityProperties;
import org.lemon.commons.security.filter.GlobalExceptionFilter;
import org.lemon.commons.security.filter.TokenAuthenticationFilter;
import org.lemon.commons.security.login.captcha.CaptchaValidationFilter;
import org.lemon.commons.security.login.username.UsernameAuthenticationFilter;
import org.lemon.commons.security.login.username.UsernameAuthenticationProvider;
import org.lemon.commons.security.service.CaptchaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity
@AutoConfiguration(after = SecurityBeanConfig.class)
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {

    /**
     * 注入过滤链异常处理Bean
     */
    private final FilterRegistrationBean<GlobalExceptionFilter> filterFilterRegistrationBean;

    /**
     * 登录验证过滤链
     *
     * @param http                           基于Web的请求配置类
     * @param usernameAuthenticationProvider 用户名密码登录Bean
     * @param authenticationSuccessHandler   登录成功Bean
     * @param authenticationFailureHandler   登录失败Bean
     * @param captchaService                 验证码校验bean
     * @param tenantEnable                   是否开启租户
     * @return 登录认证过滤链
     */
    @Bean
    @Order(1)
    public SecurityFilterChain loginFilterChain(HttpSecurity http,
                                                UsernameAuthenticationProvider usernameAuthenticationProvider,
                                                AuthenticationSuccessHandler authenticationSuccessHandler,
                                                AuthenticationFailureHandler authenticationFailureHandler,
                                                CaptchaService captchaService,
                                                CaptchaProperties captchaProperties,
                                                @Value("${lemon.tenant.enable:false}") boolean tenantEnable) {
        // 禁用默认 Filter
        commonHttpSetting(http);

        PathPatternRequestMatcher.Builder matcherBuilder = PathPatternRequestMatcher.withDefaults();

        UsernameAuthenticationFilter usernameLoginFilter = new UsernameAuthenticationFilter(
                matcherBuilder.matcher(HttpMethod.POST, "/login/username"),
                new ProviderManager(usernameAuthenticationProvider),
                authenticationSuccessHandler,
                authenticationFailureHandler,
                tenantEnable
        );

        // 使用 SecurityMatcher 将此过滤链限定在 /login/* 路径下
        http.securityMatcher(matcherBuilder.matcher("/login/*"))
                .addFilterAfter(usernameLoginFilter, LogoutFilter.class)
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated());

        // 是否开启验证码
        if (captchaProperties.getEnable()) {
            http.addFilterBefore(new CaptchaValidationFilter(captchaService), UsernameAuthenticationFilter.class);
        }

        return http.build();
    }

    /**
     * Token 授权过滤链。
     * <p>
     * 授权决策完全交由 {@link AnonymousAccessAuthorizationManager} 统一处理：
     * 命中匿名白名单 → 放行；否则要求已认证。这样 {@link TokenAuthenticationFilter}
     * 仅做 Token 解析，不再维护跳过列表，白名单的唯一来源是
     * {@code @AnonymousAccess} 注解 + {@code lemon.security.permit-all-urls} 配置。
     *
     * @param http                                基于Web的请求配置类
     * @param lemonSecurityProperties             security 配置参数
     * @param authenticationEntryPoint            认证失败处理类
     * @param accessDeniedHandler                 权限不够处理器
     * @param anonymousAccessAuthorizationManager 匿名访问授权管理器
     * @return 访问授权过滤链路
     */
    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           SecurityProperties lemonSecurityProperties,
                                           AuthenticationEntryPoint authenticationEntryPoint,
                                           AccessDeniedHandler accessDeniedHandler,
                                           AnonymousAccessAuthorizationManager anonymousAccessAuthorizationManager) {
        // 禁用默认 Filter
        commonHttpSetting(http);

        // 授权规则
        http.authorizeHttpRequests(registry -> registry
                // 放行异步 dispatch（AuthorizationFilter 在每次 dispatch 都会跑）
                .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                // 兜底：由 AuthorizationManager 统一判定 [匿名白名单 OR 已认证]
                .anyRequest().access(anonymousAccessAuthorizationManager));

        // 异常处理（仅对鉴权链生效，登录链由 success/failureHandler 直接响应）
        http.exceptionHandling(c -> c
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
        );

        // Token 解析 Filter，置于 ExceptionTranslationFilter 之后；
        // 本 Filter 仅做 best-effort 解析，不抛异常、不拦截，最终授权由 AuthorizationFilter 决定。
        http.addFilterAfter(new TokenAuthenticationFilter(lemonSecurityProperties), ExceptionTranslationFilter.class);

        return http.build();
    }

    /**
     * 禁用默认 Filter（两条过滤链公共基础配置）
     *
     * @param http 基于Web的请求配置类
     */
    private void commonHttpSetting(HttpSecurity http) {
        // 基础安全配置（使用 Lambda DSL）
        http
                // 禁用表单登录
                .formLogin(AbstractHttpConfigurer::disable)
                // 禁用登出
                .logout(AbstractHttpConfigurer::disable)
                // 禁用 CSRF（无状态 API 无需 Session）
                .csrf(AbstractHttpConfigurer::disable)
                // 禁用匿名用户
                .anonymous(AbstractHttpConfigurer::disable)
                // 前后端分离不需要‘记住我’功能
                .rememberMe(AbstractHttpConfigurer::disable)
                // 开启跨域（推荐单独配置 CORS 策略）
                .cors(Customizer.withDefaults())
                // 基于 token 机制，所以不需要 Session
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 禁用请求缓存, requestCache 用于重定向，前后端分析项目无需重定向，requestCache也用不上
                .requestCache(cache -> cache.requestCache(new NullRequestCache()))
                .headers(c -> c
                        // 禁用 X-Frame-Options
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                        // 可选：禁用 HSTS
                        .httpStrictTransportSecurity(HeadersConfigurer.HstsConfig::disable)
                );

        // 拦截项目自定义过滤器(Filter)抛出的异常.尽量提前加载。
        Objects.requireNonNull(filterFilterRegistrationBean.getFilter(),
                "GlobalSpringSecurityExceptionFilter must not be null");
        http.addFilterBefore(filterFilterRegistrationBean.getFilter(), SecurityContextHolderFilter.class);
    }
}
