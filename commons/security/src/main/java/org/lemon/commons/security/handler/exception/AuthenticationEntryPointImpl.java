package org.lemon.commons.security.handler.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.lemon.commons.core.domain.result.R;
import org.lemon.commons.core.utils.ServletUtils;
import org.lemon.commons.json.utils.JsonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;

/**
 * 未认证访问处理器（401）
 * <p>
 * 实现 {@link AuthenticationEntryPoint}，当用户未登录或 Token 失效时访问受保护资源，
 * 由 {@link ExceptionTranslationFilter} 调用本类，向客户端返回 HTTP 401 及 JSON 格式的错误信息。
 * <p>
 * 与 {@link AccessDeniedHandlerImpl} 的区别：本类处理"未认证"（未登录 / Token 失效），
 * 后者处理"已认证但无权限"（403）。
 *
 * @author lemon
 */
@Slf4j
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull AuthenticationException e) {
        log.debug("没有登录,不能访问 URL: {} ", request.getRequestURI(), e);
        String data = JsonUtils.toJsonString(R.fail("没有登录,不能访问 url: %s".formatted(request.getRequestURI())));
        ServletUtils.renderString(response, HttpStatus.UNAUTHORIZED.value(), data);
    }
}
