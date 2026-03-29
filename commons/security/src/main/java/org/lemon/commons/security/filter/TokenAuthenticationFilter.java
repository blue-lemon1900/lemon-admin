package org.lemon.commons.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.lemon.commons.redis.utils.RedisUtils;
import org.lemon.commons.security.config.properties.LemonSecurityProperties;
import org.lemon.commons.security.data.LoginUserInfo;
import org.lemon.commons.security.utils.AuthenticationUtil;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

import static org.lemon.commons.core.constant.GlobalConstants.ACCESS_TOKEN;

/**
 * Token 过滤器，验证 token 的有效性
 * 验证通过后，获得 {@link LoginUserInfo} 信息，并加入到 SpringSecurity 上下文
 *
 * @author : Lemon
 * @date : 2025-05-06 13:34
 **/
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final LemonSecurityProperties securityProperties;

    /**
     * 免认证路径匹配器：匹配所有标注了 @PermitAll 及配置文件中 permit-all-urls 的接口。
     * 命中时 {@link #shouldNotFilter} 返回 true，本过滤器直接跳过，不做 Token 校验。
     */
    private final RequestMatcher permitAllMatcher;

    /**
     * 对标注了 @PermitAll 或配置为免认证的接口跳过本过滤器，
     * 避免客户端携带无效/过期 Token 访问公开接口（如刷新令牌）时被误拦截。
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return permitAllMatcher.matches(request);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 获取请求中的token
        String token = AuthenticationUtil.resolveAuthorization(request, securityProperties.getTokenHeader(), securityProperties.getTokenParameter());
        if (StringUtils.isNoneBlank(token)) {
            // 根据缓存中的key获取用户信息
            Object cacheObject = RedisUtils.getCacheObject(ACCESS_TOKEN + token);

            // 判断是否获取到用户信息
            if (cacheObject instanceof LoginUserInfo loginUserInfo) {

                // 设置登录用户
                setLoginUser(loginUserInfo, request);
            } else {
                throw new AuthenticationServiceException("无效token,认证失败");
            }
        }

        // 继续过滤链
        filterChain.doFilter(request, response);
    }

    /**
     * 设置当前用户
     *
     * @param loginUserInfo 登录用户
     * @param request       请求
     */
    private void setLoginUser(LoginUserInfo loginUserInfo, HttpServletRequest request) {
        // 创建 UsernamePasswordAuthenticationToken 对象，并设置到上下文
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUserInfo, null, Collections.emptyList());

        // 通过 request 对象生成一些 Web 环境相关的认证细节（如客户端 IP 地址和会话信息）供 Spring Security 在认证过程中使用。
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
