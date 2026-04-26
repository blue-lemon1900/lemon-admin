package org.lemon.commons.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记接口允许匿名访问（免登录、免 Token）。
 * <p>
 * 启动时由 {@code AnnotationUtil} 扫描收集，注册进
 * {@code AnonymousAccessAuthorizationManager}，作为 URL 白名单的唯一来源。
 * <p>
 * 与 JSR-250 的 {@link jakarta.annotation.security.PermitAll} 区分：本注解仅用于
 * URL 层放行标记，不参与方法级安全（method security）判定，避免双重语义。
 *
 * @author lemon
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AnonymousAccess {
}
