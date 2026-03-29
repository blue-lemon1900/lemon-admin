package org.lemon.commons.security.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;

/**
 * 账号密码验证异常
 *
 * @author Lemon
 * @date 2026-03-29 15:54
 **/
public class BadPasswordException extends AuthenticationException {
    /**
     * Constructs an {@code AuthenticationException} with the specified message and no
     * root cause.
     *
     * @param msg the detail message
     */
    public BadPasswordException(@Nullable String msg) {
        super(msg);
    }
}
