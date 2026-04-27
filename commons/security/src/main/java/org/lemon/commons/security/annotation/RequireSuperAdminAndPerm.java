package org.lemon.commons.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

/**
 * 要求当前用户同时满足：「超级管理员」角色 + 指定权限码 的方法级安全注解。
 * <p>
 * 等价 SpEL：
 * {@code @PreAuthorize("hasRole(T(TenantConstants).SUPER_ADMIN_ROLE_KEY) and hasAuthority('{value}')")}。
 * <p>
 *
 * @author lemon
 * @see RequireTenantAdminAndPerm
 * @see PreAuthorize
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY) and hasAuthority('{value}')")
public @interface RequireSuperAdminAndPerm {

    /**
     * 必须拥有的权限码（如 {@code system:tenant:list}）。
     * 解析时被注入到 SpEL 表达式中的 {@code hasAuthority('{value}')} 位置。
     */
    String value();
}
