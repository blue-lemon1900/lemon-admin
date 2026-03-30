package org.lemon.commons.security.login.username;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.lemon.commons.security.data.LoginUserInfo;
import org.lemon.commons.security.exception.BadPasswordException;
import org.lemon.commons.security.service.UsernameService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 帐号密码登录认证
 */
@Slf4j
@RequiredArgsConstructor
public class UsernameAuthenticationProvider implements AuthenticationProvider {

    private final UsernameService usernameService;

    private final PasswordEncoder passwordEncoder;

    private final boolean tenantEnable;

    @Override
    public Authentication authenticate(@NonNull Authentication authentication) throws AuthenticationException {
        if (authentication instanceof UsernameAuthentication auth) {
            LoginUserInfo userInfo;

            // 判断是否启用了租户模式,调用不同的 Service 方法
            if (tenantEnable) {
                userInfo = usernameService.loadUserByUsername(auth.getTenantId(), auth.getUsername());
            } else {
                userInfo = usernameService.loadUserByUsername(auth.getUsername());
            }

            /*
             * 密码错误，直接抛异常。
             * 根据SpringSecurity框架的代码逻辑，认证失败时，应该抛这个异常：{@link AuthenticationException} {@link BadCredentialsException} 就是这个类的子类
             * 抛出异常后后，AuthenticationFailureHandler 的实现类会处理这个异常。
             * */
            if (!passwordEncoder.matches(auth.getPassword(), userInfo.getPassword())) {
                throw new BadPasswordException("用户名或密码错误");
            }

            // 存储用户信息到redis中
            usernameService.saveUserInfo(userInfo);

            // 认证成功后调用此方法,设置用户基本信息,并设置认证成功
            auth.onAuthenticationSuccess(userInfo);
            return auth;
        }

        log.warn("不支持的认证类型: {}", authentication.getClass().getName());
        throw new InternalAuthenticationServiceException("不支持的认证类型");
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return UsernameAuthentication.class.isAssignableFrom(authentication);
    }
}
