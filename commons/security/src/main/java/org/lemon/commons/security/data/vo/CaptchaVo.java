package org.lemon.commons.security.data.vo;

import lombok.Data;

/**
 * 验证码信息
 *
 * @author Michelle.Chung
 */
@Data
public class CaptchaVo {

    /**
     * 是否开启验证码
     */
    private Boolean captchaEnabled = true;

    /**
     * 验证码对应的 UUID 键值
     */
    private String uuid;

    /**
     * 验证码图片
     */
    private String img;
}
