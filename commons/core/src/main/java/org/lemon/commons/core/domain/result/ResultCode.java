package org.lemon.commons.core.domain.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * APi返回的状态码表
 *
 * @author jiangch
 * @since 2023/5/27
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(HttpStatus.OK.value(), "成功"),

    /**
     * 失败
     */
    FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "失败"),

    /**
     * 警告
     */
    WARN(601, "警告");

    private final Integer code;
    private final String msg;
}
