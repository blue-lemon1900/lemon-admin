package org.lemon.commons.security.data.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.lemon.commons.security.data.LoginUserInfo;

import java.io.Serial;
import java.io.Serializable;

/**
 * 认证成功后返回给前端的用户信息模版
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@AutoMapper(target = LoginUserInfo.class)
public class AuthLoginRespVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 租户编号
     */
    private String tenantId;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;
}
