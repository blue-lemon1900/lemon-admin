package org.lemon.system.domain.bo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 在线用户列表查询条件。
 * <p>对应 {@code GET /system/online/list} 的查询参数（字段为 null 即不过滤）。</p>
 */
@Data
public class OnlineUserQueryBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名模糊匹配（不区分大小写，子串包含即命中）
     */
    private String username;
}
