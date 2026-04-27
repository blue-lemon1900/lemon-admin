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

            // 密码 hash 升级：若库里 hash 不是当前 idForEncode，则用新算法重新编码并写回
            // (例如老 {bcrypt} 串首次登录后悄默升级为 {argon2})。失败仅 warn 不阻断登录。
            upgradePasswordIfNeeded(userInfo, auth.getPassword());

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

    /**
     * 在校验通过后，按需把数据库密码升级到当前 idForEncode 算法。
     * <p>
     * 等价于 {@code DaoAuthenticationProvider} 在配合
     * {@code UserDetailsPasswordService} 时的内置升级链路；本项目使用自研
     * Provider，故在此手动触发。
     */
    private void upgradePasswordIfNeeded(LoginUserInfo userInfo, String rawPassword) {
        try {
            if (!passwordEncoder.upgradeEncoding(userInfo.getPassword())) {
                return;
            }
            String newHash = passwordEncoder.encode(rawPassword);
            usernameService.updatePassword(userInfo.getUserId(), newHash);
            userInfo.setPassword(newHash);
            log.info("用户 {} 的密码 hash 已自动升级到新算法", userInfo.getUsername());
        } catch (Exception e) {
            // 升级失败不能影响登录主流程
            log.warn("用户 {} 密码 hash 升级失败,本次登录跳过升级: {}", userInfo.getUsername(), e.getMessage());
        }
    }
}
