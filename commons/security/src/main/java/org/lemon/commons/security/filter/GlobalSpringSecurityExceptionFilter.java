package org.lemon.commons.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.lemon.commons.core.domain.result.R;
import org.lemon.commons.core.utils.ServletUtils;
import org.lemon.commons.json.utils.JsonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 拦截自定义Filter 异常信息
 *
 * @author : Lemon
 * @date : 2025-05-09 22:41
 **/
@Slf4j
public class GlobalSpringSecurityExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) {
        try {
            filterChain.doFilter(request, response);
        } catch (AuthenticationException | AccessDeniedException e) {
            // 放行，让 ExceptionTranslationFilter 和对应 handler 处理
            throw e;
        } catch (Exception exception) {
            log.error("捕获 Filter 过滤链路全局异常:{}", exception.getMessage(), exception);
            ServletUtils.renderString(response, HttpStatus.INTERNAL_SERVER_ERROR.value(), JsonUtils.toJsonString(R.fail(exception.getMessage())));
        }
    }
}
