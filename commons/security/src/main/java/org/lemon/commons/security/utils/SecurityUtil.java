package org.lemon.commons.security.utils;

import cn.hutool.core.collection.CollUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.lemon.commons.core.constant.SystemConstants;
import org.lemon.commons.core.constant.TenantConstants;
import org.lemon.commons.redis.utils.RedisUtils;
import org.lemon.commons.security.data.LoginUserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.stream.Stream;

/**
 * 安全服务工具类
 *
 * @author lemon
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtil {

    /**
     * 获得当前认证信息
     *
     * @return 认证信息
     */
    public static Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        return context.getAuthentication();
    }

    /**
     * 获取当前用户
     *
     * @return 当前用户
     */
    public static LoginUserInfo getLoginUser() {
        Authentication authentication = getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof LoginUserInfo loginUser) {
                return loginUser;
            }
        }
        return null;
    }

    /**
     * 根据 token 获取用户信息
     *
     * @param token token值
     * @return 从 redis 缓存中获取用户信息
     */
    public static LoginUserInfo getLoginUser(String token) {
        return RedisUtils.getCacheObject(token);
    }

    /**
     * 获得当前用户的编号，从上下文中
     *
     * @return 用户编号
     */
    public static Long getLoginUserId() {
        LoginUserInfo loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUserId() : null;
    }

    /**
     * 获取当前请求用户的 token 值
     *
     * @return 当前tokenValue
     */
    public static String getTokenValue() {
        LoginUserInfo loginUser = getLoginUser();
        return loginUser != null ? loginUser.getAccessToken() : null;
    }

    /**
     * 获得当前用户的昵称，从上下文中
     *
     * @return 昵称
     */
    public static String getLoginUserNickname() {
        LoginUserInfo loginUser = getLoginUser();
        return loginUser != null ? loginUser.getNickname() : null;
    }

    /**
     * 获得当前用户的部门编号，从上下文中
     *
     * @return 部门编号
     */
    public static Long getLoginUserDeptId() {
        LoginUserInfo loginUser = getLoginUser();
        return loginUser != null ? loginUser.getDeptId() : null;
    }

    /**
     * 是否为超级管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isSuperAdmin(Long userId) {
        return SystemConstants.SUPER_ADMIN_ID.equals(userId);
    }

    /**
     * 是否为超级管理员
     *
     * @return 结果
     */
    public static boolean isSuperAdmin() {
        return isSuperAdmin(getLoginUserId());
    }

    /**
     * 是否为租户管理员
     *
     * @param rolePermission 角色权限标识组
     * @return 结果
     */
    public static boolean isTenantAdmin(Set<String> rolePermission) {
        if (CollUtil.isEmpty(rolePermission)) {
            return false;
        }

        return Stream.of(TenantConstants.TENANT_ADMIN_ROLE_KEY,TenantConstants.SUPER_ADMIN_ROLE_KEY).anyMatch(rolePermission::contains);
    }

    /**
     * 是否为租户管理员
     *
     * @return 结果
     */
    public static boolean isTenantAdmin() {
        LoginUserInfo loginUser = getLoginUser();
        if (loginUser == null) {
            return false;
        }
        return isTenantAdmin(loginUser.getPermissions());
    }

    /**
     * 获取租户ID
     */
    public static String getTenantId() {
        LoginUserInfo loginUser = getLoginUser();
        return loginUser != null ? loginUser.getTenantId() : null;
    }

    /**
     * 检查当前用户是否已登录
     *
     * @return 结果
     */
    public static boolean isLogin() {
        return getLoginUser() != null;
    }
}
