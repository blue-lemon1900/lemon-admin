package org.lemon.commons.security.service.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.lemon.commons.security.data.LoginUserInfo;
import org.lemon.commons.security.data.model.RoleModel;
import org.lemon.commons.security.service.SecurityFrameworkService;
import org.lemon.commons.security.utils.SecurityUtil;
import org.springframework.security.access.AccessDeniedException;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 默认的 {@link SecurityFrameworkService} 实现类
 *
 * @author : Lemon
 * @date : 2025-05-08 15:50
 **/
public class SecurityFrameworkServiceImpl implements SecurityFrameworkService {

    @Override
    public boolean hasPermission(String permission) {
        if (StringUtils.isEmpty(permission)) {
            throw new AccessDeniedException("校验的权限值不能为空");
        }

        // 获取登录用户信息
        LoginUserInfo loginUser = SecurityUtil.getLoginUser();
        // 没有获取到用户信息表示没有权限
        if (loginUser != null) {
            Set<String> permissions = loginUser.getPermissions();
            // 权限标识不为空,且权限命中
            return permissions != null && permissions.contains(permission);
        }
        return false;
    }

    @Override
    public boolean hasAnyPermissions(String... permissions) {
        if (ArrayUtils.isEmpty(permissions)) {
            throw new AccessDeniedException("校验的权限值不能为空");
        }

        // 获取登录用户信息
        LoginUserInfo loginUser = SecurityUtil.getLoginUser();
        // 没有获取到用户信息表示没有权限
        if (loginUser != null) {
            Set<String> permissionSet = loginUser.getPermissions();
            // 使用 Stream 的 anyMatch 直接判断是否存在至少一个交集
            return Arrays.stream(permissions).anyMatch(permissionSet::contains);
        }
        return false;
    }

    @Override
    public boolean hasRole(String role) {
        if (StringUtils.isEmpty(role)) {
            throw new AccessDeniedException("校验的角色值不能为空");
        }

        // 获取登录用户信息
        LoginUserInfo loginUser = SecurityUtil.getLoginUser();
        // 登录用户或角色列表为空时直接返回 false
        if (loginUser == null || loginUser.getRoles() == null) {
            return false;
        }

        // 流式遍历，过滤掉 null，直接 anyMatch
        return loginUser.getRoles().stream()
                .map(RoleModel::getRoleKey)
                .filter(Objects::nonNull)
                .anyMatch(role::equals);
    }

    @Override
    public boolean hasAnyRoles(String... roles) {
        if (ArrayUtils.isEmpty(roles)) {
            throw new AccessDeniedException("校验的角色值不能为空");
        }

        // 获取当前登录用户
        LoginUserInfo loginUser = SecurityUtil.getLoginUser();
        // 用户未登录或角色集合为空时，直接返回 false
        if (loginUser == null || loginUser.getRoles() == null) {
            return false;
        }

        // 提取用户的角色标识集合，过滤掉可能的 null
        Set<String> userRoleKeys = loginUser.getRoles().stream()
                .map(RoleModel::getRoleKey)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 判断是否存在任意一个角色交集
        return Arrays.stream(roles)
                .filter(StringUtils::isNotBlank)    // 过滤掉空白或 null
                .anyMatch(userRoleKeys::contains);
    }
}
