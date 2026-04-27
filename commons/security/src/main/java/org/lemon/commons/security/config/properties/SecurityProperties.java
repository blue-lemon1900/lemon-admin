package org.lemon.commons.security.config.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "lemon.security")
public class SecurityProperties {

    /**
     * HTTP 请求时，访问令牌的请求 Header
     */
    @NotEmpty(message = "Token Header 不能为空")
    private String tokenHeader = "Authorization";

    /**
     * HTTP 请求时，访问令牌的请求参数
     * <p>
     * 初始目的：解决 WebSocket 无法通过 header 传参，只能通过 token 参数拼接
     */
    @NotEmpty(message = "Token Parameter 不能为空")
    private String tokenParameter = "token";

    /**
     * 免登录的 URL 列表
     */
    private String[] permitAllUrls = {};

    /**
     * PasswordEncoder 当前用于"新密码编码"的算法 id（即 DelegatingPasswordEncoder 的 idForEncode）。
     * <p>
     * 切换该值即可在不动代码的前提下迁移加密算法：新密码会用新算法编码，
     * 老密码（带 {@code {bcrypt}} / {@code {pbkdf2}} 等前缀）仍能正常校验，
     * 并在用户首次登录时由 UsernameAuthenticationProvider 触发自动升级。
     * <p>
     * 取值范围与 {@code SecurityBeanConfig.passwordEncoder()} 中注册的 encoder map 一致：
     * {@code argon2} / {@code bcrypt} / {@code pbkdf2} / {@code scrypt}。
     */
    @Pattern(regexp = "argon2|bcrypt|pbkdf2|scrypt", message = "passwordEncoderId 必须是 argon2/bcrypt/pbkdf2/scrypt 之一")
    private String passwordEncoderId = "argon2";
}
