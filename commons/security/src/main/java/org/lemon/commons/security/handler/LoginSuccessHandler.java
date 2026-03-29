package org.lemon.commons.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.lemon.commons.core.domain.result.R;
import org.lemon.commons.core.utils.MapstructUtils;
import org.lemon.commons.core.utils.ServletUtils;
import org.lemon.commons.json.utils.JsonUtils;
import org.lemon.commons.security.data.LoginUserInfo;
import org.lemon.commons.security.data.vo.AuthLoginRespVO;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * 认证成功处理器
 * <p>
 * 实现 {@link AuthenticationSuccessHandler}，在用户登录成功后向客户端返回 JSON 格式的
 * 认证信息（accessToken、refreshToken、过期时间及用户基本信息等）。
 * <p>
 * 注意：Spring Security 过滤器链运行在 Spring MVC 的 DispatcherServlet 之前，
 * 无法使用 ResponseEntity 等高级抽象，因此通过 {@link ServletUtils#renderString} 直接写出响应。
 *
 * @author lemon
 */
@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Authentication authentication) {

        // 获取登录认证成功后的登录信息
        Object principal = authentication.getPrincipal();

        if (principal instanceof LoginUserInfo currentUser) {
            AuthLoginRespVO respVO = MapstructUtils.convert(currentUser, AuthLoginRespVO.class);
            ServletUtils.renderString(response, HttpStatus.OK.value(), JsonUtils.toJsonString(R.success(respVO)));
        } else {
            throw new SecurityException("登录认证成功后，authentication.getPrincipal() 返回的对象必须是：LoginUserInfo.");
        }
    }
}
