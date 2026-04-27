package org.lemon.commons.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

/**
 * 要求当前用户满足：「超级管理员」或「租户管理员」任一角色 + 指定权限码 的方法级安全注解。
 * <p>
 * 等价 SpEL：
 * {@code @PreAuthorize("hasAnyRole(T(TenantConstants).SUPER_ADMIN_ROLE_KEY,
 * T(TenantConstants).TENANT_ADMIN_ROLE_KEY) and hasAuthority('{value}')")}。
 * <p>
 *
 * @author lemon
 * @see RequireSuperAdminAndPerm
 * @see PreAuthorize
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY, T(org.lemon.commons.core.constant.TenantConstants).TENANT_ADMIN_ROLE_KEY) and hasAuthority('{value}')")
public @interface RequireTenantAdminAndPerm {

    /**
     * 必须拥有的权限码（如 {@code system:menu:list}）。
     * 解析时被注入到 SpEL 表达式中的 {@code hasAuthority('{value}')} 位置。
     */
    String value();
}
