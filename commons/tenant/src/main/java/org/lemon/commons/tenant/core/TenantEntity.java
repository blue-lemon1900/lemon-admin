package org.lemon.commons.tenant.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.lemon.commons.mybatis.core.domain.BaseEntity;

/**
 * 租户基类
 *
 * @author Michelle.Chung
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantEntity extends BaseEntity {

    /**
     * 租户编号
     */
    private String tenantId;

}
