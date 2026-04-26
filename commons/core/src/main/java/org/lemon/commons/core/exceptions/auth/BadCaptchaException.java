package org.lemon.commons.core.exceptions.auth;

/**
 * 验证码异常
 *
 * @author : Lemon
 * @date : 2025-05-06 17:52
 **/
public class BadCaptchaException extends RuntimeException {

    public BadCaptchaException(String message) {
        super(message);
    }
}
