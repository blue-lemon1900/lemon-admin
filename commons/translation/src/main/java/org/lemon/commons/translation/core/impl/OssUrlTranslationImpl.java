package org.lemon.commons.translation.core.impl;

import lombok.AllArgsConstructor;
import org.lemon.commons.core.service.OssService;
import org.lemon.commons.translation.annotation.TranslationType;
import org.lemon.commons.translation.constant.TransConstant;
import org.lemon.commons.translation.core.TranslationInterface;

/**
 * OSS翻译实现
 *
 * @author Lion Li
 */
@AllArgsConstructor
@TranslationType(type = TransConstant.OSS_ID_TO_URL)
public class OssUrlTranslationImpl implements TranslationInterface<String> {

    private final OssService ossService;

    @Override
    public String translation(Object key, String other) {
        if (key instanceof String ids) {
            return ossService.selectUrlByIds(ids);
        } else if (key instanceof Long id) {
            return ossService.selectUrlByIds(id.toString());
        }
        return null;
    }
}
