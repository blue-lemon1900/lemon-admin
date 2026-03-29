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
}
