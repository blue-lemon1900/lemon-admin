package org.lemon.commons.security.data.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.lemon.commons.security.data.LoginUserInfo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 在线用户列表展示对象
 * <p>由 {@code OnlineUserService.listOnline()} 输出，用于管理端「在线用户」页面渲染。</p>
 */
@Data
@AutoMapper(target = LoginUserInfo.class)
public class OnlineUserVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;

    private String username;

    private String nickname;

    private String tenantId;

    private String deptName;

    /**
     * 该端的 access token（踢人入参）
     */
    private String accessToken;

    private LocalDateTime loginTime;

    private String loginIp;

    private String userAgent;
}
