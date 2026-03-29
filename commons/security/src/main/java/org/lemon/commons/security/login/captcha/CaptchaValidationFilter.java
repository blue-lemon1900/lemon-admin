package org.lemon.commons.security.login.captcha;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.lemon.commons.json.utils.JsonUtils;
import org.lemon.commons.security.exception.BadCaptchaException;
import org.lemon.commons.security.service.CaptchaService;
import org.lemon.commons.security.utils.AuthenticationUtil;
import org.lemon.commons.security.wrapper.RepeatedlyRequestWrapper;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

import static org.lemon.commons.json.utils.JsonUtils.TYPE_REF_STRING;
import static org.lemon.commons.security.constant.AuthenticationConstant.CAPTCHA_KEY;
import static org.lemon.commons.security.constant.AuthenticationConstant.UUID_KEY;

/**
 * 验证码校验
 *
 * @author : Lemon
 * @date : 2025-05-06 17:44
 **/
@RequiredArgsConstructor
public class CaptchaValidationFilter extends OncePerRequestFilter {


    private final CaptchaService captchaService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 校验请求类型
        AuthenticationUtil.validateRequest(request);

        // 使用缓存包装类解决请求体重复读取问题
        RepeatedlyRequestWrapper wrappedRequest = new RepeatedlyRequestWrapper(request);

        // 提取请求数据
        Map<String, String> requestMap = JsonUtils.convert(wrappedRequest.getReader(), TYPE_REF_STRING);

        String captcha = AuthenticationUtil.extractCredential(requestMap, CAPTCHA_KEY, "验证码是必填项");
        String uuid = AuthenticationUtil.extractCredential(requestMap, UUID_KEY, "验证码 UUID 键是必填项");

        if (!captchaService.validate(captcha, uuid)) {
            throw new BadCaptchaException("验证码错误");
        }

        // 校验通过，继续后续流程
        filterChain.doFilter(wrappedRequest, response);
    }
}
