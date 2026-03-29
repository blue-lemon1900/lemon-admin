package org.lemon.commons.security.login.username;

import lombok.Getter;
import lombok.Setter;
import org.lemon.commons.security.data.LoginUserInfo;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;

/**
 * 用户名密码认证信息
 * <p>
 * SpringSecurity 传输登录认证的数据的载体，相当一个Dto
 * 必须是 {@link Authentication} 实现类
 * 这里选择extends{@link AbstractAuthenticationToken}，而不是直接 implements Authentication,
 * 是为了少些写代码。因为{@link Authentication}定义了很多接口，我们用不上。
 */
@Setter
@Getter
public class UsernameAuthentication extends AbstractAuthenticationToken {

    /**
     * 租户 ID（可为空，为空时走非租户登录逻辑）
     */
    private String tenantId;

    /**
     * 用户名称（认证前从客户端传入）
     */
    private String username;

    /**
     * 用户密码（认证后应被清理）
     */
    private String password;

    /**
     * 当前登录用户信息（认证成功后由 AuthenticationProvider 填充）
     */
    private LoginUserInfo currentUser;

    public UsernameAuthentication(String username, String password) {
        super(Collections.emptyList());
        this.username = username;
        this.password = password;
    }

    public UsernameAuthentication(String tenantId, String username, String password) {
        super(Collections.emptyList());
        this.tenantId = tenantId;
        this.username = username;
        this.password = password;
    }

    @Override
    public Object getCredentials() {
        // 根据SpringSecurity的设计，授权成后，Credential（比如，登录密码）信息需要被清空
        return isAuthenticated() ? null : password;
    }

    @Override
    public Object getPrincipal() {
        // 根据 SpringSecurity 的设计，授权成功之前，返回的客户端传过来的数据。授权成功后，返回当前登陆用户的信息
        return isAuthenticated() ? currentUser : username;
    }

    /**
     * 认证成功后主动清除密码
     */
    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        // 显式清理密码
        this.password = null;
    }

    /**
     * 重写 setAuthenticated 方法，限制非法调用
     * 直接暴露 setAuthenticated 方法可能导致认证状态被外部篡改
     *
     * @param isAuthenticated 如果令牌应该被信任，则为 <code> true </code>；如果令牌不应该被信任，则为 <code> false </code>
     * @throws IllegalArgumentException
     */
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("认证状态只能通过 AuthenticationProvider 设置");
        }
        super.setAuthenticated(Boolean.FALSE);
    }

    /**
     * 认证成功后调用此方法
     *
     * @param userInfo 当前登录用户信息
     */
    public void onAuthenticationSuccess(LoginUserInfo userInfo) {
        this.currentUser = userInfo;
        // 把密码制空
        this.currentUser.setPassword(null);
        // 认证状态设置为True
        super.setAuthenticated(Boolean.TRUE);
    }
}
