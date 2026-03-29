package org.lemon.commons.tenant.exception;

import org.lemon.commons.core.exceptions.base.BaseException;

import java.io.Serial;

/**
 * 租户异常类
 *
 * @author Lion Li
 */
public class TenantException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    public TenantException(String code, Object... args) {
        super("tenant", code, args, null);
    }
}
