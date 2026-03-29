package org.lemon.commons.encrypt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.lemon.commons.core.exceptions.ServiceException;
import org.lemon.commons.core.utils.StringUtils;
import org.lemon.commons.encrypt.annotation.ApiEncrypt;
import org.lemon.commons.encrypt.properties.ApiDecryptProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.Objects;

/**
 * Crypto 过滤器
 *
 * @author wdhcr
 */
@RequiredArgsConstructor
public class CryptoFilter extends HttpFilter {

    private final ApiDecryptProperties properties;

    private final HandlerExceptionResolver exceptionResolver;

    private final RequestMappingHandlerMapping handlerMapping;

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 获取加密注解
        ApiEncrypt apiEncrypt = this.getApiEncryptAnnotation(request);
        boolean responseFlag = apiEncrypt != null && apiEncrypt.response();
        ServletRequest requestWrapper = null;
        ServletResponse responseWrapper = null;

        // 是否为 put 或者 post 请求
        if (HttpMethod.PUT.matches(request.getMethod()) || HttpMethod.POST.matches(request.getMethod())) {
            // 是否存在加密标头
            String headerValue = request.getHeader(properties.getHeaderFlag());
            if (StringUtils.isNotBlank(headerValue)) {
                // 请求解密
                requestWrapper = new DecryptRequestBodyWrapper(request, properties.getPrivateKey(), properties.getHeaderFlag());
            } else {
                // 是否有注解，有就报错，没有放行
                if (apiEncrypt != null) {
                    exceptionResolver.resolveException(
                            request, response, null,
                            new ServiceException("没有访问权限，请联系管理员授权", HttpStatus.FORBIDDEN.value())
                    );
                    return;
                }
            }
        }

        EncryptResponseBodyWrapper responseBodyWrapper = null;
        // 判断是否响应加密
        if (responseFlag) {
            responseBodyWrapper = new EncryptResponseBodyWrapper(response);
            responseWrapper = responseBodyWrapper;
        }

        // 放行请求
        chain.doFilter(Objects.requireNonNullElse(requestWrapper, request), Objects.requireNonNullElse(responseWrapper, response));

        if (responseFlag) {
            response.reset();
            // 对原始内容加密
            String encryptContent = responseBodyWrapper.getEncryptContent(response, properties.getPublicKey(), properties.getHeaderFlag());
            // 对加密后的内容写出
            response.getWriter().write(encryptContent);
        }
    }

    /**
     * 获取 ApiEncrypt 注解
     */
    private ApiEncrypt getApiEncryptAnnotation(HttpServletRequest request) {
        // 获取注解
        try {
            HandlerExecutionChain mappingHandler = handlerMapping.getHandler(request);
            if (mappingHandler != null) {
                Object handler = mappingHandler.getHandler();
                // 从handler获取注解
                if (handler instanceof HandlerMethod handlerMethod) {
                    return handlerMethod.getMethodAnnotation(ApiEncrypt.class);
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
