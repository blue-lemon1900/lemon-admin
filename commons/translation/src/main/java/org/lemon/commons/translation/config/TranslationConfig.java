package org.lemon.commons.translation.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.lemon.commons.translation.annotation.TranslationType;
import org.lemon.commons.translation.core.TranslationInterface;
import org.lemon.commons.translation.core.handler.TranslationBeanSerializerModifier;
import org.lemon.commons.translation.core.handler.TranslationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.module.SimpleModule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 翻译模块配置类
 *
 * @author Lion Li
 */
@Slf4j
@AutoConfiguration
public class TranslationConfig {

    @Autowired
    private List<TranslationInterface<?>> list;

    @PostConstruct
    public void init() {
        Map<String, TranslationInterface<?>> map = new HashMap<>(list.size());
        for (TranslationInterface<?> trans : list) {
            if (trans.getClass().isAnnotationPresent(TranslationType.class)) {
                TranslationType annotation = trans.getClass().getAnnotation(TranslationType.class);
                map.put(annotation.type(), trans);
            } else {
                log.warn(trans.getClass().getName() + " 翻译实现类未标注 TranslationType 注解!");
            }
        }
        TranslationHandler.TRANSLATION_MAPPER.putAll(map);
    }

    /**
     * 注册翻译模块：通过声明 JacksonModule Bean，Spring Boot 自动将其注册到 ObjectMapper。
     * 在 module 的 setupModule 中添加 ValueSerializerModifier，
     * 使标注了 @Translation 的字段在值为 null 时也由 TranslationHandler 处理。
     */
    @Bean
    public JacksonModule translationModule() {
        return new SimpleModule() {
            @Override
            public void setupModule(SetupContext context) {
                super.setupModule(context);
                context.addSerializerModifier(new TranslationBeanSerializerModifier());
            }
        };
    }

}
