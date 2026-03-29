package org.lemon.system.controller;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lemon.commons.core.domain.result.R;
import org.lemon.commons.security.data.vo.CaptchaVo;
import org.lemon.commons.security.service.CaptchaService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码操作处理
 *
 * @author Lion Li
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class CaptchaController {

    private final CaptchaService captchaService;

    /**
     * 生成验证码
     */
    @PermitAll
    @GetMapping("/code")
    public R<CaptchaVo> getCode() {
        return R.success(captchaService.getCode());
    }
}
