package org.lemon.commons.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 验证码异常
 *
 * @author : Lemon
 * @date : 2025-05-06 17:52
 **/
public class BadCaptchaException extends AuthenticationException {

    public BadCaptchaException(String message) {
        super(message);
    }
}
