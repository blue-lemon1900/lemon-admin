package org.lemon.commons.security.utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import jakarta.annotation.security.PermitAll;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 注解工具类：用于查找方法或类上注解。
 *
 * @author : Lemon
 * @date : 2025-05-07 20:17
 **/
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnnotationUtil {

    /**
     * 提取所有被 @PermitAll 注解标记的接口 URL，并按 HTTP 方法分类返回。
     * <p>
     * 实现逻辑：
     * 1. 遍历所有注册的控制器方法。
     * 2. 检查方法或类级别是否包含 @PermitAll 注解。
     * 3. 收集匹配的 URL 模式及其支持的 HTTP 方法。
     * <p>
     * 典型用途：
     * 在 Spring Security 配置中，将 permitAll 的 URL 动态加入白名单，避免硬编码。
     *
     * @param handlerMapping Spring MVC 的请求映射处理器，用于获取所有控制器方法映射信息
     * @return Multimap 结构：Key 为 HttpMethod 类型，Value 为对应允许匿名访问的 URL 集合
     * 例如：{ GET: ["/api/public", "/docs"], POST: ["/login"] }
     * <p>
     * 注：
     * <ul>
     *   <li>URL 模式已合并类和方法级别的 @RequestMapping 路径（如 "/api" + "/user" -> "/api/user"）</li>
     *   <li>若未明确指定 HTTP 方法（如 @RequestMapping 未设置 method），则默认匹配所有方法，需结合业务场景处理</li>
     *   <li>支持 Ant 风格路径（如 "/resources/**"）</li>
     * </ul>
     */
    public static Multimap<HttpMethod, String> getPermitAllUrls(RequestMappingHandlerMapping handlerMapping) {
        Multimap<HttpMethod, String> result = HashMultimap.create();

        // 获取 HandlerMapping 并强制转型
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            RequestMappingInfo mappingInfo = entry.getKey();

            // 检查方法或类上是否标注了 @PermitAll
            boolean methodLevel = handlerMethod.hasMethodAnnotation(PermitAll.class);
            boolean classLevel = handlerMethod.getBeanType().isAnnotationPresent(PermitAll.class);
            if (!methodLevel && !classLevel) {
                continue;
            }

            // 提取 PathPattern 路径
            PathPatternsRequestCondition pathPatternsCondition = mappingInfo.getPathPatternsCondition();
            if (pathPatternsCondition != null) {
                Set<String> urls = pathPatternsCondition.getPatterns()
                        .stream()
                        .map(PathPattern::getPatternString)
                        // 将空字符串映射为 "/"
                        .map(url -> StringUtils.isBlank(url) ? "/" : url)
                        .collect(Collectors.toSet());
                if (urls.isEmpty()) continue;

                // 将 Spring 的 RequestMethod 枚举映射为标准的 HttpMethod 枚举。
                Set<RequestMethod> methods = mappingInfo.getMethodsCondition().getMethods();
                if (methods.isEmpty()) {
                    // 未指定请求类型时，默认支持所有请求类型（需根据业务调整）
                    Arrays.stream(RequestMethod.values()).forEach(method -> result.putAll(mapToHttpMethod(method), urls));
                } else {
                    methods.forEach(method -> result.putAll(mapToHttpMethod(method), urls));
                }
            }
        }
        return result;
    }

    /**
     * 将 Spring 的 {@link RequestMethod} 枚举映射为标准的 {@link HttpMethod} 枚举。
     *
     * <p>此方法用于统一不同框架或场景下 HTTP 方法枚举的差异，例如将 Spring MVC 的请求方法类型
     * 转换为 Spring Web 或 WebFlux 通用的 {@link HttpMethod} 类型。</p>
     *
     * @param requestMethod Spring 定义的请求方法枚举（通常来自 {@code @RequestMapping} 等注解）
     * @return 对应的标准 HTTP 方法枚举，映射规则如下：
     * <ul>
     *   <li>{@link RequestMethod#GET} → {@link HttpMethod#GET}</li>
     *   <li>{@link RequestMethod#POST} → {@link HttpMethod#POST}</li>
     *   <li>{@link RequestMethod#PUT} → {@link HttpMethod#PUT}</li>
     *   <li>{@link RequestMethod#DELETE} → {@link HttpMethod#DELETE}</li>
     *   <li>{@link RequestMethod#HEAD} → {@link HttpMethod#HEAD}</li>
     *   <li>{@link RequestMethod#PATCH} → {@link HttpMethod#PATCH}</li>
     *   <li>{@link RequestMethod#OPTIONS} → {@link HttpMethod#OPTIONS}</li>
     *   <li>{@link RequestMethod#TRACE} → {@link HttpMethod#TRACE}</li>
     * </ul>
     * @throws IllegalArgumentException 如果传入未明确处理的 {@code RequestMethod} 类型
     * @implNote 当前实现已覆盖所有 Spring 定义的 {@code RequestMethod} 枚举值，
     * 若未来 Spring 扩展新的方法类型，此方法需同步更新
     */
    private static HttpMethod mapToHttpMethod(RequestMethod requestMethod) {
        return switch (requestMethod) {
            case GET -> HttpMethod.GET;
            case POST -> HttpMethod.POST;
            case PUT -> HttpMethod.PUT;
            case DELETE -> HttpMethod.DELETE;
            case HEAD -> HttpMethod.HEAD;
            case PATCH -> HttpMethod.PATCH;
            case OPTIONS -> HttpMethod.OPTIONS;
            case TRACE -> HttpMethod.TRACE;
        };
    }
}
