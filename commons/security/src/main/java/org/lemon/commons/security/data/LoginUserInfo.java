package org.lemon.commons.security.data;

import lombok.Data;
import org.lemon.commons.security.data.model.RoleModel;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * 用户信息,存储到 Redis 中的对象
 */
@Data
public class LoginUserInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 部门Id
     */
    private Long deptId;

    /**
     * 数据权限校验时会用到,当前角色ID
     */
    private Long roleId;

    /**
     * 租户Id
     */
    private String tenantId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 角色信息
     */
    private Set<RoleModel> roles;

    /**
     * 权限信息
     */
    private Set<String> permissions;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;
}
