package org.lemon.system.domain.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 刷新令牌请求 Bo
 */
@Data
public class RefreshTokenBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 刷新令牌
     */
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}
