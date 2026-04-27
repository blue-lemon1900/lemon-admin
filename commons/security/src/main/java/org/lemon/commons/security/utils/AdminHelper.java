package org.lemon.commons.security.utils;

import cn.hutool.core.collection.CollUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.lemon.commons.core.constant.SystemConstants;
import org.lemon.commons.core.constant.TenantConstants;
import org.lemon.commons.security.data.LoginUserInfo;

import java.util.Set;
import java.util.stream.Stream;

/**
 * 业务管理员判定助手（超级管理员 / 租户管理员）。
 * <p>
 * 仅承担「基于业务规则判断当前用户/指定用户是否管理员」一类职责，规则与项目业务紧耦合：
 * <ul>
 *   <li>超级管理员 = 用户 ID 等于 {@link SystemConstants#SUPER_ADMIN_ID}</li>
 *   <li>租户管理员 = 角色 key 集合包含 {@link TenantConstants#SUPER_ADMIN_ROLE_KEY}
 *       或 {@link TenantConstants#TENANT_ADMIN_ROLE_KEY}</li>
 * </ul>
 * 不涉及 SecurityContext 读取（见 {@link SecurityContextHelper}），
 * 不涉及通用权限/角色判断（见 {@link AuthorityHelper}）。
 *
 * @author lemon
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminHelper {

    /**
     * 指定用户 ID 是否为超级管理员。
     */
    public static boolean isSuperAdmin(Long userId) {
        return SystemConstants.SUPER_ADMIN_ID.equals(userId);
    }

    /**
     * 当前登录用户是否为超级管理员。
     */
    public static boolean isSuperAdmin() {
        return isSuperAdmin(SecurityContextHelper.getLoginUserId());
    }

    /**
     * 给定角色 key 集合是否命中租户管理员（含超级管理员）。
     */
    public static boolean isTenantAdmin(Set<String> rolePermission) {
        if (CollUtil.isEmpty(rolePermission)) {
            return false;
        }
        return Stream.of(TenantConstants.TENANT_ADMIN_ROLE_KEY, TenantConstants.SUPER_ADMIN_ROLE_KEY)
                .anyMatch(rolePermission::contains);
    }

    /**
     * 当前登录用户是否为租户管理员（含超级管理员）。
     * <p>
     * 注意：与 {@link LoginUserInfo#getPermissions()} 保持一致，使用 permissions 集合判断
     * （历史实现如此；语义上更接近"角色 key 是否在 permissions 里"）。
     */
    public static boolean isTenantAdmin() {
        LoginUserInfo loginUser = SecurityContextHelper.getLoginUser();
        if (loginUser == null) {
            return false;
        }
        return isTenantAdmin(loginUser.getPermissions());
    }
}
