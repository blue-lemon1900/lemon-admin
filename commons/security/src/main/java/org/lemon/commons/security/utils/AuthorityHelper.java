package org.lemon.commons.security.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.lemon.commons.security.data.LoginUserInfo;
import org.lemon.commons.security.data.model.RoleModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

/**
 * 权限/角色相关助手。
 * <p>
 * 承担两类职责：
 * <ul>
 *   <li>{@link LoginUserInfo} → {@link GrantedAuthority} 集合的纯转换（{@link #buildAuthorities}）</li>
 *   <li>当前登录用户的权限码 / 角色 key 命中判断（{@link #hasAnyAuthority} / {@link #hasAnyRole}），
 *       语义等价于 SpEL 的 {@code hasAnyAuthority(...)} 与 {@code hasAnyRole(...)}</li>
 * </ul>
 * 不涉及登录用户字段读取（见 {@link SecurityContextHelper}），
 * 不涉及业务管理员判定（见 {@link AdminHelper}）。
 *
 * @author lemon
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorityHelper {

    /**
     * 角色 GrantedAuthority 前缀。Spring Security 内置 {@code hasRole(...)} 会自动比对带此前缀的 authority。
     */
    public static final String ROLE_PREFIX = "ROLE_";

    /**
     * 把 {@link LoginUserInfo} 中的权限码与角色 key 转换为 Spring Security 的 GrantedAuthority 集合。
     * <p>
     * - 权限码原样作为 authority(供 {@code hasAuthority(...)} 匹配)<br>
     * - 角色 key 加 {@link #ROLE_PREFIX} 前缀(供 {@code hasRole(...)} 匹配)
     * <p>
     * 纯函数：不依赖 {@link SecurityContextHolder SecurityContext}，可独立测试。
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
     * Java 代码中判断当前用户是否拥有任一权限码,语义等价于 SpEL 的 {@code hasAnyAuthority(...)}。
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
     * Java 代码中判断当前用户是否拥有任一角色 key,语义等价于 SpEL 的 {@code hasAnyRole(...)};
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
        Authentication auth = SecurityContextHelper.getAuthentication();
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
