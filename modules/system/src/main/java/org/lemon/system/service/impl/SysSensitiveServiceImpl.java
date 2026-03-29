package org.lemon.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.lemon.commons.security.service.SecurityFrameworkService;
import org.lemon.commons.security.utils.SecurityUtil;
import org.lemon.commons.sensitive.core.SensitiveService;
import org.springframework.stereotype.Service;

/**
 * 脱敏服务
 * 默认管理员不过滤
 * 需自行根据业务重写实现
 *
 * @author Lion Li
 * @version 3.6.0
 */
@Service
@RequiredArgsConstructor
public class SysSensitiveServiceImpl implements SensitiveService {

    private final SecurityFrameworkService ss;

    /**
     * 是否脱敏
     */
    @Override
    public boolean isSensitive(String[] roleKey, String[] perms) {
        if (!SecurityUtil.isLogin()) {
            return true;
        }

        boolean roleExist = ArrayUtils.isNotEmpty(roleKey);
        boolean permsExist = ArrayUtils.isNotEmpty(perms);

        if (roleExist && permsExist && ss.hasAnyRoles(roleKey) && ss.hasAnyPermissions(perms)) {
            return false;
        } else if (roleExist && ss.hasAnyRoles(roleKey)) {
            return false;
        } else if (permsExist && ss.hasAnyPermissions(perms)) {
            return false;
        }

        // 超级管理员不用脱敏
        return !SecurityUtil.isSuperAdmin();
    }

}
