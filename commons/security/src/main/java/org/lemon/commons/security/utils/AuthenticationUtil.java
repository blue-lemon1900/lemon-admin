package org.lemon.commons.security.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.lemon.commons.core.utils.StringUtils;
import org.lemon.commons.security.constant.AuthenticationConstant;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;

import java.util.Map;

/**
 * Authentication 工具类
 *
 * @author : Lemon
 * @date : 2025-05-08 11:21
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationUtil {

    private static final String SUPPORTED_METHOD = "POST";
    private static final MediaType SUPPORTED_CONTENT_TYPE = MediaType.APPLICATION_JSON;

    /**
     * 从请求中解析认证 Token，先尝试从 Header 中获取，Header 中不存在则从请求参数中获取
     *
     * @param request       请求
     * @param headerName    认证 Token 对应的 Header 名字
     * @param parameterName 认证 Token 对应的 Parameter 名字
     * @return 认证 Token
     */
    public static String resolveAuthorization(HttpServletRequest request, String headerName, String parameterName) {
        // 优先从 Header 中获取 Token，否则从参数中获取
        String token = request.getHeader(headerName);
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(parameterName);

            if (StringUtils.isBlank(token)) {
                return null;
            }
        }

        // 去除 "Bearer " 前缀（含空格）
        String prefix = AuthenticationConstant.AUTHORIZATION_BEARER;
        return token.startsWith(prefix) ? token.substring(prefix.length()).trim() : token.trim();
    }

    /**
     * 获取 map 的值
     *
     * @param dataMap      map对象
     * @param key          获取的键
     * @param errorMessage 异常提示
     * @return 如果获取到值去除前后空格返回, 没有获取到就抛出异常信息
     */
    public static String extractCredential(Map<String, String> dataMap, String key, String errorMessage) {
        String value = dataMap.get(key);
        if (StringUtils.isEmpty(value)) {
            throw new AuthenticationServiceException(errorMessage);
        }
        return value.trim();
    }

    /**
     * 校验请求必须是指定的 HTTP 方法，且 Content-Type 必须一致，
     * 否则抛 AuthenticationServiceException。
     */
    public static void validateRequest(HttpServletRequest request) {
        // 方法检查
        if (!SUPPORTED_METHOD.equalsIgnoreCase(request.getMethod())) {
            throw new AuthenticationServiceException(
                    String.format("仅支持 %s 请求方法", SUPPORTED_METHOD));
        }
        // Content-Type 检查（忽略 charset 等附加参数，仅匹配 type/subtype）
        String contentTypeStr = request.getContentType();
        if (contentTypeStr == null || !SUPPORTED_CONTENT_TYPE.equalsTypeAndSubtype(MediaType.parseMediaType(contentTypeStr))) {
            throw new AuthenticationServiceException(
                    String.format("不支持的内容类型，必须为 %s", SUPPORTED_CONTENT_TYPE));
        }
    }
}
