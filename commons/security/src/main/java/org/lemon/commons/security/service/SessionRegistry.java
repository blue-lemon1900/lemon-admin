package org.lemon.commons.security.service;

import org.lemon.commons.security.data.LoginUserInfo;

/**
 * 在线会话索引「写边」。
 * <p>
 * 仅供登录链路使用：登录成功后注册、登出/续期时注销。
 * 维护 {@code userId → 该用户全部 access token} 的反向索引，使后续可以按 userId 整端踢人。
 * <p>
 * Redis 布局：
 * <pre>
 *   global:access_token:{token}   → LoginUserInfo            (单 token TTL)
 *   global:refresh_token:{token}  → LoginUserInfo            (单 refresh token TTL)
 *   global:online_user:{userId}   → Set&lt;accessToken&gt;        (= refresh token TTL，登录续期)
 * </pre>
 *
 * @see OnlineSessionAdmin 读边 + 管理操作
 */
public interface SessionRegistry {

    /**
     * 注册一次登录会话：把 {@code accessToken} 加入 {@code online_user:{userId}} 集合。
     * <p>调用前需保证 {@link LoginUserInfo#getAccessToken()} 与 {@code refreshToken}
     * 已生成并写入对应 access/refresh key。</p>
     */
    void register(LoginUserInfo loginUserInfo);

    /**
     * 注销一次登录会话：删除该 token 对应的 access/refresh key，并从 {@code online_user:{userId}} 集合移除。
     * <p>用于 {@code logout} 与 {@code updateToken} 的旧 token 清理。</p>
     */
    void unregister(LoginUserInfo loginUserInfo);

    /**
     * 把指定用户的全部活跃会话一次性下线（不带"自踢"护栏，专供登录链路 / 内部使用）。
     * <p>用于实现「单端登录」语义：登录时把该用户的旧会话先全部清理，再发新 token。</p>
     * <p>与 {@code OnlineSessionAdmin.kickByUserId} 的差别：后者带「不能踢自己」护栏给管理后台用，
     * 此处不带护栏（登录链路里 SecurityContext 还没有当前用户，且语义是"系统主动顶替旧会话"
     * 而非"管理员踢人"）。</p>
     *
     * @return 实际下线的会话数（0 表示该用户此前没有活跃会话）
     */
    int unregisterAll(Long userId);
}
