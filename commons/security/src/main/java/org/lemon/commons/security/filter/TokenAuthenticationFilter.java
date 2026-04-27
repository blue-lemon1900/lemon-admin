package org.lemon.commons.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.lemon.commons.redis.utils.RedisUtils;
import org.lemon.commons.security.config.properties.LemonSecurityProperties;
import org.lemon.commons.security.data.LoginUserInfo;
import org.lemon.commons.security.utils.AuthenticationUtil;
import org.lemon.commons.security.utils.SecurityUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.lemon.commons.core.constant.GlobalConstants.ACCESS_TOKEN;

/**
 * Token 解析过滤器（best-effort）。
 * <p>
 * 仅负责解析 Token 并填充 {@link SecurityContextHolder}，不做拦截判断：
 * <ul>
 *   <li>无 Token → 不设置 Authentication，直接放行</li>
 *   <li>Token 有效 → 设置 Authentication，放行</li>
 *   <li>Token 无效/过期 → 不设置 Authentication，放行（记录 warn）</li>
 * </ul>
 * 是否拦截完全由后续的 {@code AuthorizationFilter} 委托
 * {@code AnonymousAccessAuthorizationManager} 决策：
 * 公开接口（如 /auth/refresh）允许携带过期 Token 访问；受保护接口在未认证时返回 401。
 *
 * @author : Lemon
 * @date : 2025-05-06 13:34
 **/
@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final LemonSecurityProperties securityProperties;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = AuthenticationUtil.resolveAuthorization(request, securityProperties.getTokenHeader(), securityProperties.getTokenParameter());
        if (StringUtils.isNotBlank(token)) {
            Object cacheObject = RedisUtils.getCacheObject(ACCESS_TOKEN + token);
            if (cacheObject instanceof LoginUserInfo loginUserInfo) {
                setLoginUser(loginUserInfo, request);
            } else {
                log.warn("无效或过期 Token，URL: {}", request.getRequestURI());
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 设置当前用户
     *
     * @param loginUserInfo 登录用户
     * @param request       请求
     */
    private void setLoginUser(LoginUserInfo loginUserInfo, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginUserInfo, null, SecurityUtil.buildAuthorities(loginUserInfo));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
