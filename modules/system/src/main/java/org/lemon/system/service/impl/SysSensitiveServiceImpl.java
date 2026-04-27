package org.lemon.system.service.impl;

import org.apache.commons.lang3.ArrayUtils;
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
public class SysSensitiveServiceImpl implements SensitiveService {

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

        if (roleExist && permsExist && SecurityUtil.hasAnyRole(roleKey) && SecurityUtil.hasAnyAuthority(perms)) {
            return false;
        } else if (roleExist && SecurityUtil.hasAnyRole(roleKey)) {
            return false;
        } else if (permsExist && SecurityUtil.hasAnyAuthority(perms)) {
            return false;
        }

        // 超级管理员不用脱敏
        return !SecurityUtil.isSuperAdmin();
    }

}
