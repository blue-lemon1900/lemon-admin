package org.lemon.system.service.impl;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Strings;
import org.lemon.commons.core.config.properties.CaptchaProperties;
import org.lemon.commons.core.constant.Constants;
import org.lemon.commons.ratelimiter.annotation.RateLimiter;
import org.lemon.commons.ratelimiter.enums.LimitType;
import org.lemon.commons.redis.utils.RedisUtils;
import org.lemon.commons.security.data.vo.CaptchaVo;
import org.lemon.commons.security.service.CaptchaService;
import org.lemon.commons.web.core.WaveAndCircleCaptcha;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.Duration;

import static org.lemon.commons.core.constant.GlobalConstants.CAPTCHA_CODE_KEY;

/**
 * 验证码服务 实现类
 *
 * @author : Lemon
 * @date : 2025-05-08 11:33
 **/
@RequiredArgsConstructor
@Service
public class SysCaptchaServiceImpl implements CaptchaService {

    private final CaptchaProperties captchaProperties;

    @Override
    public CaptchaVo getCode() {
        boolean captchaEnabled = captchaProperties.getEnable();
        if (!captchaEnabled) {
            CaptchaVo captchaVo = new CaptchaVo();
            captchaVo.setCaptchaEnabled(false);
            return captchaVo;
        }

        return getCodeImpl();
    }

    /**
     * 生成验证码
     * 独立方法避免验证码关闭之后仍然走限流
     */
    @RateLimiter(time = 60, count = 10, limitType = LimitType.IP)
    public CaptchaVo getCodeImpl() {
        // 保存验证码信息
        String uuid = IdUtil.simpleUUID();
        String verifyKey = CAPTCHA_CODE_KEY + uuid;
        // 生成验证码
        String captchaType = captchaProperties.getType();
        CodeGenerator codeGenerator;
        if ("math".equals(captchaType)) {
            codeGenerator = new MathGenerator(captchaProperties.getNumberLength(), false);
        } else {
            codeGenerator = new RandomGenerator(captchaProperties.getCharLength());
        }
        WaveAndCircleCaptcha captcha = new WaveAndCircleCaptcha(160, 60);
        // captcha.setBackground(Color.WHITE); // 不设置就是透明底
        captcha.setFont(new Font("Arial", Font.BOLD, 45));
        captcha.setGenerator(codeGenerator);
        captcha.createCode();
        // 如果是数学验证码，使用SpEL表达式处理验证码结果
        String code = captcha.getCode();
        if ("math".equals(captchaType)) {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(Strings.CS.remove(code, "="));
            code = exp.getValue(String.class);
        }
        RedisUtils.setCacheObject(verifyKey, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION));
        CaptchaVo captchaVo = new CaptchaVo();
        captchaVo.setUuid(uuid);
        captchaVo.setImg(captcha.getImageBase64());
        return captchaVo;
    }

    @Override
    public boolean validate(String captcha, String uuid) {
        String verifyKey = CAPTCHA_CODE_KEY + uuid;

        // 从缓存中读验证码
        String value = RedisUtils.getCacheObject(verifyKey);
        return Strings.CS.equals(value, captcha);
    }
}
