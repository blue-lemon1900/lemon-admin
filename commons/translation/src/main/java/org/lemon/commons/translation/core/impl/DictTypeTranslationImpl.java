package org.lemon.commons.translation.core.impl;

import lombok.AllArgsConstructor;
import org.lemon.commons.core.service.DictService;
import org.lemon.commons.core.utils.StringUtils;
import org.lemon.commons.translation.annotation.TranslationType;
import org.lemon.commons.translation.constant.TransConstant;
import org.lemon.commons.translation.core.TranslationInterface;

/**
 * 字典翻译实现
 *
 * @author Lion Li
 */
@AllArgsConstructor
@TranslationType(type = TransConstant.DICT_TYPE_TO_LABEL)
public class DictTypeTranslationImpl implements TranslationInterface<String> {

    private final DictService dictService;

    @Override
    public String translation(Object key, String other) {
        if (key instanceof String dictValue && StringUtils.isNotBlank(other)) {
            return dictService.getDictLabel(other, dictValue);
        }
        return null;
    }
}
