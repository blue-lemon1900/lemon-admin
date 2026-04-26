package org.lemon.commons.security.authorization;

import com.google.common.collect.Multimap;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.lemon.commons.security.annotation.AnonymousAccess;
import org.lemon.commons.security.config.properties.LemonSecurityProperties;
import org.lemon.commons.security.utils.AnnotationUtil;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * 匿名访问授权管理器。
 * <p>
 * 启动时一次性收集白名单（{@link AnonymousAccess}
 * 注解 + 配置文件 {@code lemon.security.permit-all-urls}），构建为单个
 * {@link OrRequestMatcher}。
 * <p>
 * 请求期判定：命中白名单 → 放行；否则要求已认证。是否拦截完全由本管理器决策，
 * 因此 {@code TokenAuthenticationFilter} 无需再维护跳过列表。
 *
 * @author lemon
 */
@Component
@RequiredArgsConstructor
public class AnonymousAccessAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final RequestMappingHandlerMapping handlerMapping;
    private final LemonSecurityProperties securityProperties;

    private RequestMatcher anonymousMatcher;

    @PostConstruct
    public void init() {
        PathPatternRequestMatcher.Builder builder = PathPatternRequestMatcher.withDefaults();
        List<RequestMatcher> matchers = new ArrayList<>();

        Multimap<HttpMethod, String> annotated = AnnotationUtil.getAnonymousAccessUrls(handlerMapping);
        annotated.forEach((method, path) -> matchers.add(builder.matcher(method, path)));

        Arrays.stream(securityProperties.getPermitAllUrls()).forEach(path -> matchers.add(builder.matcher(path)));

        this.anonymousMatcher = matchers.isEmpty() ? request -> false : new OrRequestMatcher(matchers);
    }

    @Override
    public AuthorizationResult authorize(@NonNull Supplier<? extends Authentication> authentication, RequestAuthorizationContext context) {
        if (anonymousMatcher.matches(context.getRequest())) {
            return new AuthorizationDecision(true);
        }
        Authentication auth = authentication.get();
        return new AuthorizationDecision(auth != null && auth.isAuthenticated());
    }
}
