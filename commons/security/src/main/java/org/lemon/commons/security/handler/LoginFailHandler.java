package org.lemon.commons.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.lemon.commons.core.domain.result.R;
import org.lemon.commons.core.utils.ServletUtils;
import org.lemon.commons.json.utils.JsonUtils;
import org.lemon.commons.security.exception.BadPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * 登录失败处理器
 * <p>
 * 实现 {@link AuthenticationFailureHandler}，当 {@code AbstractAuthenticationProcessingFilter}
 * 抛出 {@link AuthenticationException}（如密码错误、账号不存在等）时，
 * 向客户端返回 HTTP 401 及 JSON 格式的错误信息。
 *
 * @author lemon
 */
@Slf4j
public class LoginFailHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, AuthenticationException exception) {
        log.debug("登录认证失败: {}", exception.getMessage());

        if (exception instanceof BadPasswordException) {
            ServletUtils.renderString(response, HttpStatus.INTERNAL_SERVER_ERROR.value(), JsonUtils.toJsonString(R.fail(exception.getMessage())));
        } else {
            ServletUtils.renderString(response, HttpStatus.UNAUTHORIZED.value(), JsonUtils.toJsonString(R.fail(exception.getMessage())));
        }
    }
}
