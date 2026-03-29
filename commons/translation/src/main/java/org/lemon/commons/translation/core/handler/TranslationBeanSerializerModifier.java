package org.lemon.commons.translation.core.handler;

import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.ValueSerializerModifier;

import java.util.List;

/**
 * Bean 序列化修改器 解决 Null 被单独处理问题
 * <p>
 * NOTE: Jackson 3.x 中 BeanSerializerModifier 已重命名为 ValueSerializerModifier
 *
 * @author Lion Li
 */
public class TranslationBeanSerializerModifier extends ValueSerializerModifier {

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription.Supplier beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter writer : beanProperties) {
            // 如果序列化器为 TranslationHandler 的话 将 Null 值也交给他处理
            if (writer.getSerializer() instanceof TranslationHandler serializer) {
                writer.assignNullSerializer(serializer);
            }
        }
        return beanProperties;
    }

}
