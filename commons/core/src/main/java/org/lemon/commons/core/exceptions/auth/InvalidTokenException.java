package org.lemon.commons.core.exceptions.auth;

import java.io.Serial;

/**
 * 令牌无效异常（refresh token 不存在或已过期）
 */
public class InvalidTokenException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidTokenException(String message) {
        super(message);
    }
}
