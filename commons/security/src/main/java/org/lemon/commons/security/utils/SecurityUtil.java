package org.lemon.commons.security.utils;

import cn.hutool.core.collection.CollUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.lemon.commons.core.constant.SystemConstants;
import org.lemon.commons.core.constant.TenantConstants;
import org.lemon.commons.redis.utils.RedisUtils;
import org.lemon.commons.security.data.LoginUserInfo;
import org.lemon.commons.security.data.model.RoleModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
     * 角色 GrantedAuthority 前缀。Spring Security 内置 hasRole(...) 会自动比对带此前缀的 authority。
     */
    public static final String ROLE_PREFIX = "ROLE_";

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

        return Stream.of(TenantConstants.TENANT_ADMIN_ROLE_KEY, TenantConstants.SUPER_ADMIN_ROLE_KEY).anyMatch(rolePermission::contains);
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

    /**
     * 把 {@link LoginUserInfo} 中的权限码与角色 key 转换为 Spring Security 的 GrantedAuthority 集合。
     * <p>
     * - 权限码原样作为 authority(供 hasAuthority(...) 匹配)
     * - 角色 key 加 ROLE_ 前缀(供 hasRole(...) 匹配)
     */
    public static Collection<GrantedAuthority> buildAuthorities(LoginUserInfo userInfo) {
        if (userInfo == null) {
            return Collections.emptyList();
        }
        Stream<SimpleGrantedAuthority> permStream = userInfo.getPermissions() == null
                ? Stream.empty()
                : userInfo.getPermissions().stream()
                  .filter(StringUtils::isNotBlank)
                  .map(SimpleGrantedAuthority::new);
        Stream<SimpleGrantedAuthority> roleStream = userInfo.getRoles() == null
                ? Stream.empty()
                : userInfo.getRoles().stream()
                  .map(RoleModel::getRoleKey)
                  .filter(StringUtils::isNotBlank)
                  .map(k -> new SimpleGrantedAuthority(ROLE_PREFIX + k));
        return Stream.<GrantedAuthority>concat(permStream, roleStream).toList();
    }

    /**
     * Java 代码中判断当前用户是否拥有任一权限码,语义等价于 SpEL 的 hasAnyAuthority(...)。
     */
    public static boolean hasAnyAuthority(String... authorities) {
        if (authorities == null || authorities.length == 0) {
            return false;
        }
        Set<String> owned = currentAuthoritySet();
        return Arrays.stream(authorities)
                .filter(StringUtils::isNotBlank)
                .anyMatch(owned::contains);
    }

    /**
     * Java 代码中判断当前用户是否拥有任一角色 key,语义等价于 SpEL 的 hasAnyRole(...);
     * 内部会自动加 {@link #ROLE_PREFIX} 前缀。
     */
    public static boolean hasAnyRole(String... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }
        Set<String> owned = currentAuthoritySet();
        return Arrays.stream(roles)
                .filter(StringUtils::isNotBlank)
                .map(r -> ROLE_PREFIX + r)
                .anyMatch(owned::contains);
    }

    private static Set<String> currentAuthoritySet() {
        Authentication auth = getAuthentication();
        if (auth == null) {
            return Collections.emptySet();
        }
        Collection<? extends GrantedAuthority> list = auth.getAuthorities();
        if (list.isEmpty()) {
            return Collections.emptySet();
        }
        return AuthorityUtils.authorityListToSet(list);
    }
}
