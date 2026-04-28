package org.lemon.commons.core.constant;

/**
 * 全局的key常量 (业务无关的key)
 *
 * @author Lion Li
 */
public interface GlobalConstants {

    /**
     * 全局 redis key (业务无关的key)
     */
    String GLOBAL_REDIS_KEY = "global:";

    /**
     * 验证码 redis key
     */
    String CAPTCHA_CODE_KEY = GLOBAL_REDIS_KEY + "captcha_codes:";

    /**
     * 防重提交 redis key
     */
    String REPEAT_SUBMIT_KEY = GLOBAL_REDIS_KEY + "repeat_submit:";

    /**
     * 限流 redis key
     */
    String RATE_LIMIT_KEY = GLOBAL_REDIS_KEY + "rate_limit:";

    /**
     * 三方认证 redis key
     */
    String SOCIAL_AUTH_CODE_KEY = GLOBAL_REDIS_KEY + "social_auth_codes:";

    /**
     * 认证token
     */
    String ACCESS_TOKEN = GLOBAL_REDIS_KEY + "access_token:";

    /**
     * 刷新token
     */
    String REFRESH_TOKEN = GLOBAL_REDIS_KEY + "refresh_token:";

    /**
     * 在线用户索引：value 为该用户当前持有的所有 access token 集合
     * <p>用途：支持「按 userId 一键踢出全部端」「列出在线用户」</p>
     */
    String ONLINE_USER = GLOBAL_REDIS_KEY + "online_user:";
}
