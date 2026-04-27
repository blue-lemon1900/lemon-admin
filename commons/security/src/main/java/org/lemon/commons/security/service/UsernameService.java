package org.lemon.commons.security.service;

import org.lemon.commons.security.data.LoginUserInfo;

/**
 * 用户名密码登录相关的接口
 *
 * @author : Lemon
 * @date : 2025-05-06 18:43
 **/
public interface UsernameService {

    /**
     * 根据用户名称查询用户信息
     *
     * @param tenantId 租户Id
     * @param username 用名称
     * @return 用户基本信息
     */
    LoginUserInfo loadUserByUsername(String tenantId, String username);

    /**
     * 根据用户名称查询用户信息
     *
     * @param username 用名称
     * @return 用户基本信息
     */
    LoginUserInfo loadUserByUsername(String username);

    /**
     * 保存登录用户信息(到 redis 中)
     *
     * @param loginUserInfo 登录的用户新
     */
    void saveUserInfo(LoginUserInfo loginUserInfo);

    /**
     * 退出登录
     */
    void logout();

    /**
     * 重新刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 刷新后的登录用户信息（含新的 accessToken 和 refreshToken）
     */
    LoginUserInfo updateToken(String refreshToken);

    /**
     * 更新用户的密码 hash（用于密码算法平滑升级）。
     * <p>
     * 由 {@code UsernameAuthenticationProvider} 在密码校验通过且
     * {@code PasswordEncoder.upgradeEncoding(...)} 返回 {@code true} 时触发，
     * 把数据库里的旧前缀 hash（如 {@code {bcrypt}...}）替换为
     * 当前 idForEncode（{@code {argon2}...}）重新编码后的新 hash。
     *
     * @param userId          用户 ID
     * @param newPasswordHash 已编码的新密码 hash（含 {@code {id}} 前缀）
     */
    void updatePassword(Long userId, String newPasswordHash);
}
