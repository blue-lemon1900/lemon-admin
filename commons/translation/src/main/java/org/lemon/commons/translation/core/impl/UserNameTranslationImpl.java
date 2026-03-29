package org.lemon.commons.translation.core.impl;

import cn.hutool.core.convert.Convert;
import lombok.AllArgsConstructor;
import org.lemon.commons.core.service.UserService;
import org.lemon.commons.translation.annotation.TranslationType;
import org.lemon.commons.translation.constant.TransConstant;
import org.lemon.commons.translation.core.TranslationInterface;

/**
 * 用户名翻译实现
 *
 * @author Lion Li
 */
@AllArgsConstructor
@TranslationType(type = TransConstant.USER_ID_TO_NAME)
public class UserNameTranslationImpl implements TranslationInterface<String> {

    private final UserService userService;

    @Override
    public String translation(Object key, String other) {
        return userService.selectUserNameById(Convert.toLong(key));
    }
}
