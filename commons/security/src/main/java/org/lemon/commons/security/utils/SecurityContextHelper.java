package org.lemon.commons.security.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.lemon.commons.redis.utils.RedisUtils;
import org.lemon.commons.security.data.LoginUserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * SecurityContext 访问与当前登录用户读取助手。
 * <p>
 * 仅承担「从 Spring Security 上下文读取当前会话状态」一类职责：
 * <ul>
 *   <li>{@link Authentication} 实例获取</li>
 *   <li>当前登录用户 {@link LoginUserInfo} 及其常用字段(userId / nickname / deptId / tenantId / token)读取</li>
 *   <li>是否已登录的判断</li>
 * </ul>
 * 不涉及权限/角色判断（见 {@link AuthorityHelper}），不涉及业务管理员判定（见 {@link AdminHelper}）。
 *
 * @author lemon
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityContextHelper {

    /**
     * 获得当前认证信息。未登录时返回 {@code null}。
     */
    public static Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        return context.getAuthentication();
    }

    /**
     * 获取当前登录用户。未登录或 principal 类型不匹配时返回 {@code null}。
     */
    public static LoginUserInfo getLoginUser() {
        Authentication authentication = getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUserInfo loginUser) {
            return loginUser;
        }
        return null;
    }

    /**
     * 根据 token 从 Redis 缓存获取用户信息。
     *
     * @param token 缓存 key(完整的 redis key)
     */
    public static LoginUserInfo getLoginUser(String token) {
        return RedisUtils.getCacheObject(token);
    }

    /**
     * 当前登录用户 ID。未登录返回 {@code null}。
     */
    public static Long getLoginUserId() {
        LoginUserInfo loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUserId() : null;
    }

    /**
     * 当前请求携带的访问 token。未登录返回 {@code null}。
     */
    public static String getTokenValue() {
        LoginUserInfo loginUser = getLoginUser();
        return loginUser != null ? loginUser.getAccessToken() : null;
    }

    /**
     * 当前登录用户昵称。未登录返回 {@code null}。
     */
    public static String getLoginUserNickname() {
        LoginUserInfo loginUser = getLoginUser();
        return loginUser != null ? loginUser.getNickname() : null;
    }

    /**
     * 当前登录用户的部门编号。未登录返回 {@code null}。
     */
    public static Long getLoginUserDeptId() {
        LoginUserInfo loginUser = getLoginUser();
        return loginUser != null ? loginUser.getDeptId() : null;
    }

    /**
     * 当前登录用户的租户 ID。未登录返回 {@code null}。
     */
    public static String getTenantId() {
        LoginUserInfo loginUser = getLoginUser();
        return loginUser != null ? loginUser.getTenantId() : null;
    }

    /**
     * 当前请求是否已登录。
     */
    public static boolean isLogin() {
        return getLoginUser() != null;
    }
}
