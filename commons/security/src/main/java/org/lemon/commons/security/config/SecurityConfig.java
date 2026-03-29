package org.lemon.commons.security.config;

import com.google.common.collect.Multimap;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lemon.commons.core.config.properties.CaptchaProperties;
import org.lemon.commons.security.config.properties.LemonSecurityProperties;
import org.lemon.commons.security.filter.GlobalSpringSecurityExceptionFilter;
import org.lemon.commons.security.filter.TokenAuthenticationFilter;
import org.lemon.commons.security.login.captcha.CaptchaValidationFilter;
import org.lemon.commons.security.login.username.UsernameAuthenticationFilter;
import org.lemon.commons.security.login.username.UsernameAuthenticationProvider;
import org.lemon.commons.security.service.CaptchaService;
import org.lemon.commons.security.utils.AnnotationUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
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
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private final FilterRegistrationBean<GlobalSpringSecurityExceptionFilter> filterFilterRegistrationBean;

    /**
     * 登录验证过滤链
     *
     * @param http                           基于Web的请求配置类
     * @param usernameAuthenticationProvider 用户名密码登录Bean
     * @param authenticationSuccessHandler   登录成功Bean
     * @param authenticationFailureHandler   登录失败Bean
     * @param captchaService                 验证码校验bean
     * @return 登录认证过滤链
     */
    @Bean
    @Order(1)
    public SecurityFilterChain loginFilterChain(HttpSecurity http,
                                                UsernameAuthenticationProvider usernameAuthenticationProvider,
                                                AuthenticationSuccessHandler authenticationSuccessHandler,
                                                AuthenticationFailureHandler authenticationFailureHandler,
                                                CaptchaService captchaService,
                                                CaptchaProperties captchaProperties) {
        commonHttpSetting(http);

        // 复用同一个 builder 实例，避免重复调用 PathPatternRequestMatcher.withDefaults()
        PathPatternRequestMatcher.Builder matcherBuilder = PathPatternRequestMatcher.withDefaults();

        UsernameAuthenticationFilter usernameLoginFilter = new UsernameAuthenticationFilter(
                matcherBuilder.matcher(HttpMethod.POST, "/login/username"),
                new ProviderManager(usernameAuthenticationProvider),
                authenticationSuccessHandler,
                authenticationFailureHandler
        );

        // 使用 SecurityMatcher 将此过滤链限定在 /login/* 路径下
        http.securityMatcher(matcherBuilder.matcher("/login/*"))
                .addFilterAfter(usernameLoginFilter, LogoutFilter.class)
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated());

        if (captchaProperties.getEnable()) {
            http.addFilterBefore(new CaptchaValidationFilter(captchaService), UsernameAuthenticationFilter.class);
        }

        return http.build();
    }

    /**
     * Token 授权过滤链
     *
     * @param http                         基于Web的请求配置类
     * @param authorizeRequestsCustomizers 自定义的权限映射 Bean
     * @param lemonSecurityProperties      security配置参数
     * @param requestMappingHandlerMapping Spring MVC 的请求映射处理器，用于获取所有控制器方法映射信息
     * @param authenticationEntryPoint     认证失败处理类
     * @param accessDeniedHandler          权限不够处理器
     * @return 访问授权过滤链路
     */
    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           List<AuthorizeRequestsCustomizer> authorizeRequestsCustomizers,
                                           RequestMappingHandlerMapping requestMappingHandlerMapping,
                                           LemonSecurityProperties lemonSecurityProperties,
                                           AuthenticationEntryPoint authenticationEntryPoint,
                                           AccessDeniedHandler accessDeniedHandler) {
        commonHttpSetting(http);

        // 提取所有标注了 @PermitAll 的接口 URL（按 HTTP 方法分类），以及配置文件中的免认证路径。
        // 这份数据同时用于：1、授权规则的 permitAll 配置；2、Token 过滤器的跳过规则。
        Multimap<HttpMethod, String> permitAllUrls = AnnotationUtil.getPermitAllUrls(requestMappingHandlerMapping);
        String[] configPermitUrls = lemonSecurityProperties.getPermitAllUrls();

        // 授权规则
        http.authorizeHttpRequests(registry -> {
            // 放行 @PermitAll 接口
            permitAllUrls.forEach((method, path) -> registry.requestMatchers(method, path).permitAll());
            // 注册业务自定义规则
            authorizeRequestsCustomizers.forEach(customizer -> customizer.customize(registry));
            // 放行异步请求
            registry.dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll();
            // 放行配置文件中指定的路径
            registry.requestMatchers(configPermitUrls).permitAll();
            // 兜底：其余请求均需认证
            registry.anyRequest().authenticated();
        });

        // 异常处理（仅对鉴权链生效，登录链由 success/failureHandler 直接响应）
        http.exceptionHandling(c -> c
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
        );

        // 添加 Token 校验 Filter，置于 ExceptionTranslationFilter 之后，
        // 确保 Token 无效时抛出的 AuthenticationException 能被 ExceptionTranslationFilter
        // 捕获并委托给 AuthenticationEntryPointImpl 返回 401，而不会被外层的
        // GlobalSpringSecurityExceptionFilter 误当作 500 处理。
        http.addFilterAfter(
                new TokenAuthenticationFilter(lemonSecurityProperties, buildPermitAllMatcher(permitAllUrls, configPermitUrls)),
                ExceptionTranslationFilter.class
        );

        return http.build();
    }

    /**
     * 构建免 Token 校验的路径匹配器。
     * <p>
     * 将 @PermitAll 接口（按 HTTP 方法细分）与配置文件中的免认证路径合并为一个 OR 匹配器，
     * 供 {@link TokenAuthenticationFilter#shouldNotFilter} 使用：命中任意规则即跳过 Token 校验。
     *
     * @param permitAllUrls  从 @PermitAll 注解提取的 URL（按 HTTP 方法分类）
     * @param configUrls     配置文件 lemon.security.permit-all-urls 中的路径（不限 HTTP 方法）
     * @return 合并后的 OR 匹配器
     */
    private RequestMatcher buildPermitAllMatcher(Multimap<HttpMethod, String> permitAllUrls, String[] configUrls) {
        PathPatternRequestMatcher.Builder matcherBuilder = PathPatternRequestMatcher.withDefaults();
        List<RequestMatcher> matchers = new ArrayList<>();
        permitAllUrls.forEach((method, path) -> matchers.add(matcherBuilder.matcher(method, path)));
        Arrays.stream(configUrls).forEach(path -> matchers.add(matcherBuilder.matcher(path)));
        return new OrRequestMatcher(matchers);
    }

    /**
     * 禁用不必要的默认filter（两条过滤链公共基础配置）
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
