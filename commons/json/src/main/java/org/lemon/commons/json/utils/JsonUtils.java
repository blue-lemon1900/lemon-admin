package org.lemon.commons.json.utils;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.lemon.commons.core.utils.spring.SpringUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JSON 工具类
 *
 * @author 芋道源码
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {
    /**
     * 转换时需要明确泛型类型
     */
    public static final TypeReference<Map<String, String>> TYPE_REF_STRING = new TypeReference<>() {
    };

    /**
     * 懒加载 ObjectMapper：每次从 Spring 容器获取，避免类加载时容器尚未就绪的问题。
     * Spring getBean 本质是一次 Map 查找，性能损耗可忽略。
     */
    private static ObjectMapper mapper() {
        return SpringUtils.getBean(ObjectMapper.class);
    }

    public static ObjectMapper getObjectMapper() {
        return mapper();
    }

    /**
     * 将对象序列化为 JSON 字符串，对象为 null 时返回 null
     */
    public static String toJsonString(Object object) {
        if (ObjectUtil.isNull(object)) {
            return null;
        }
        return mapper().writeValueAsString(object);
    }

    /**
     * 将 JSON 字符串反序列化为指定类型对象，字符串为空时返回 null
     */
    public static <T> T parseObject(String text, Class<T> clazz) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        return mapper().readValue(text, clazz);
    }

    /**
     * 将字节数组反序列化为指定类型对象，数组为空时返回 null
     */
    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        if (ArrayUtil.isEmpty(bytes)) {
            return null;
        }
        return mapper().readValue(bytes, clazz);
    }

    /**
     * 将 JSON 字符串反序列化为复杂泛型类型，字符串为空时返回 null
     */
    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        return mapper().readValue(text, typeReference);
    }

    /**
     * 将 JSON 字符串反序列化为 Dict（Map），字符串为空或不是 JSON 对象时返回 null
     */
    public static Dict parseMap(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            return mapper().readValue(text, Dict.class);
        } catch (MismatchedInputException e) {
            // 类型不匹配，说明不是 JSON 对象
            return null;
        }
    }

    /**
     * 将 JSON 字符串反序列化为 Dict 列表，字符串为空时返回 null
     */
    public static List<Dict> parseArrayMap(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        ObjectMapper m = mapper();
        return m.readValue(text, m.getTypeFactory().constructCollectionType(List.class, Dict.class));
    }

    /**
     * 将 JSON 字符串反序列化为指定类型的列表，字符串为空时返回空列表
     */
    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (StringUtils.isBlank(text)) {
            return new ArrayList<>();
        }
        ObjectMapper m = mapper();
        return m.readValue(text, m.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    /**
     * 判断字符串是否为合法 JSON（对象或数组）
     */
    public static boolean isJson(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        try {
            mapper().readTree(str);
            return true;
        } catch (JacksonException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否为 JSON 对象（{}）
     */
    public static boolean isJsonObject(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        try {
            return mapper().readTree(str).isObject();
        } catch (JacksonException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否为 JSON 数组（[]）
     */
    public static boolean isJsonArray(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        try {
            return mapper().readTree(str).isArray();
        } catch (JacksonException e) {
            return false;
        }
    }

    /**
     * @param obj           object 要转换的对象
     * @param typeReference 指定类型的TypeReference对象
     * @param <T>           目标对象的泛型类型
     * @return 转换后的对象，如果字符串为空则返回null
     */
    public static <T> T convert(Object obj, TypeReference<T> typeReference) {
        if (obj == null) {
            return null;
        }
        return parseObject(toJsonString(obj), typeReference);
    }

    /**
     * @param reader        要转换的对象
     * @param typeReference 指定类型的TypeReference对象
     * @param <T>           目标对象的泛型类型
     * @return 转换后的对象，如果字符串为空则返回null
     */
    public static <T> T convert(Reader reader, TypeReference<T> typeReference) {
        if (reader == null) {
            return null;
        }
        return mapper().readValue(reader, typeReference);
    }

    /**
     * @param obj   object 要转换的对象
     * @param clazz 要转换的目标对象类型
     * @param <T>   目标对象的泛型类型
     * @return 转换后的对象，如果字符串为空则返回null
     */
    public static <T> T convert(Object obj, Class<T> clazz) {
        if (obj == null) {
            return null;
        }
        return parseObject(toJsonString(obj), clazz);
    }
}
