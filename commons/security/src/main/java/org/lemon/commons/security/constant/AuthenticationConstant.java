package org.lemon.commons.security.constant;

/**
 * 认证相关常量
 *
 * @author : Lemon
 * @date : 2025-05-10 12:05
 **/
public interface AuthenticationConstant {

    /**
     * 用户名键
     */
    String USERNAME_KEY = "username";

    /**
     * 用户密码键
     */
    String PASSWORD_KEY = "password";

    /**
     * 租户ID键
     */
    String TENANT_KEY = "tenant";

    /**
     * 验证码
     */
    String CAPTCHA_KEY = "captcha";

    /**
     * 验证码对应的uuid(redis中存储的key)
     */
    String UUID_KEY = "uuid";

    /**
     * HEADER 认证头 value 的前缀
     */
    String AUTHORIZATION_BEARER = "Bearer ";

    /**
     * 访问令牌过期时间（分钟）
     */
    long ACCESS_TOKEN_EXPIRE_MINUTES = 10;

    /**
     * 刷新令牌过期时间（分钟）
     */
    long REFRESH_TOKEN_EXPIRE_MINUTES = 30;

}
