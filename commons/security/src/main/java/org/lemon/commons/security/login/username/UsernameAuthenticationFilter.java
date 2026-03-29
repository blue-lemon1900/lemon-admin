package org.lemon.commons.security.login.username;

import cn.hutool.core.convert.Convert;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.lemon.commons.core.utils.spring.SpringUtils;
import org.lemon.commons.json.utils.JsonUtils;
import org.lemon.commons.security.utils.AuthenticationUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.Map;

import static org.lemon.commons.json.utils.JsonUtils.TYPE_REF_STRING;
import static org.lemon.commons.security.constant.AuthenticationConstant.*;

/**
 * 提取用户名密码
 * AbstractAuthenticationProcessingFilter 的实现类要做的工作：
 * 1. 从HttpServletRequest提取授权凭证。假设用户使用 用户名/密码 登录，就需要在这里提取 username 和 password。
 * 然后，把提取到的授权凭证封装到的Authentication对象，并且 authentication.isAuthenticated() 一定返回false
 * 2. 将Authentication对象传给AuthenticationManager进行实际的授权操作
 * <p>
 * 认证流程
 * graph TD
 * A[客户端请求] --> B[UsernameAuthenticationFilter]
 * B --> C[封装UsernameAuthentication对象]
 * C --> D[AuthenticationManager]
 * D --> E[选择合适的AuthenticationProvider]
 * E --> F[执行实际认证]
 * F --> G[返回认证后的Authentication对象]
 */
@Slf4j
public class UsernameAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public UsernameAuthenticationFilter(RequestMatcher pathRequestMatcher,
                                        AuthenticationManager authenticationManager,
                                        AuthenticationSuccessHandler successHandler,
                                        AuthenticationFailureHandler failureHandler) {
        super(pathRequestMatcher);
        setAuthenticationManager(authenticationManager);
        setAuthenticationSuccessHandler(successHandler);
        setAuthenticationFailureHandler(failureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, @NonNull HttpServletResponse response) throws AuthenticationException, IOException {
        // 提取请求数据
        Map<String, String> requestMap = JsonUtils.convert(request.getReader(), TYPE_REF_STRING);

        String username = AuthenticationUtil.extractCredential(requestMap, USERNAME_KEY, "用户名是必填项");
        String password = AuthenticationUtil.extractCredential(requestMap, PASSWORD_KEY, "用户密码是必填项");

        // Spring Security 需要的认证信息对象
        UsernameAuthentication authentication;

        // 判断是否启用了租户模式
        if (Convert.toBool(SpringUtils.getProperty("tenant.enable"), true)) {
            String tenantId = AuthenticationUtil.extractCredential(requestMap, TENANT_KEY, "租户(tenant)是必填项");
            authentication = new UsernameAuthentication(tenantId, username, password);
        } else {
            authentication = new UsernameAuthentication(username, password);
        }

        // 开始登录认证。SpringSecurity 会利用 Authentication 对象去寻找 AuthenticationProvider 进行登录认证
        return getAuthenticationManager().authenticate(authentication);
    }
}
