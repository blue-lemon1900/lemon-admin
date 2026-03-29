package org.lemon.commons.security.service;

import org.lemon.commons.security.data.vo.CaptchaVo;

/**
 * 验证码接口
 *
 * @author : Lemon
 * @date : 2025-05-08 11:28
 **/
public interface CaptchaService {

    /**
     * 获取验证码信息
     *
     * @return 返回验证码视图对象
     */
    CaptchaVo getCode();

    /**
     * 校验验证码
     *
     * @param captcha 验证码
     * @param uuid    验证码UUID键
     * @return 通过返回 true 否则返回 false
     */
    boolean validate(String captcha, String uuid);
}
