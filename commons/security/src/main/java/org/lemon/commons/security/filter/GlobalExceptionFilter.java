package org.lemon.commons.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * 过滤链全局异常拦截器。
 * <p>
 * 安全异常（{@link AuthenticationException} / {@link AccessDeniedException}）透传，
 * 由 Spring Security 内置的 {@link ExceptionTranslationFilter} 委托给 EntryPoint /
 * AccessDeniedHandler 处理。
 * <p>
 * 其余异常委托给 {@link HandlerExceptionResolver}，进入项目统一的
 * {@code @RestControllerAdvice}（{@code GlobalExceptionHandler}），与 Controller
 * 异常共享同一套响应格式。
 *
 * @author : Lemon
 * @date : 2025-05-09 22:41
 **/
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (AuthenticationException | AccessDeniedException e) {
            // 安全异常透传给 ExceptionTranslationFilter，由 EntryPoint / AccessDeniedHandler 处理；
            // 抢先匹配，避免被下方 catch (Exception) 误捕获后绕开 Spring Security 的处理路径
            throw e;
        } catch (Exception e) {
            // 其余异常委托给 @RestControllerAdvice 统一处理
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
