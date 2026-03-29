package org.lemon.commons.security.handler.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.lemon.commons.core.domain.result.R;
import org.lemon.commons.core.utils.ServletUtils;
import org.lemon.commons.json.utils.JsonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;

/**
 * 无权限访问处理器（403）
 * <p>
 * 实现 {@link AccessDeniedHandler}，当已登录用户访问其无权操作的资源时，
 * 由 {@link ExceptionTranslationFilter} 调用本类，向客户端返回 HTTP 403 及 JSON 格式的错误信息。
 * <p>
 * 与 {@link AuthenticationEntryPointImpl} 的区别：本类处理"已认证但无权限"（403），
 * 后者处理"未认证"（未登录 / Token 失效，401）。
 *
 * @author lemon
 */
@Slf4j
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull AccessDeniedException e) {
        log.debug("权限不够,无法访问 url: {}", request.getRequestURI(), e);
        String data = JsonUtils.toJsonString(R.fail("权限不够,无法访问 url: %s ".formatted(request.getRequestURI())));
        ServletUtils.renderString(response, HttpStatus.FORBIDDEN.value(), data);
    }
}
