package org.lemon.commons.validation.dicts;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lemon.commons.core.utils.spring.SpringUtils;
import org.lemon.commons.systemapi.domain.dto.DictDataDTO;
import org.lemon.commons.systemapi.domain.dto.DictTypeDTO;
import org.lemon.commons.systemapi.service.DictService;
import org.lemon.commons.validation.config.ValidatorConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DictPattern + DictPatternValidator 集成测试。
 * 因为 DictPatternValidator 通过 SpringUtils.getBean(DictService.class) 反查 Bean,
 * 必须在 Spring 上下文中运行。这里挂载最小上下文 (ValidatorConfig + SpringUtils + 假的 DictService)。
 */
@SpringBootTest(
        classes = {
                ValidatorConfig.class,
                SpringUtils.class,
                DictPatternValidatorTest.MockConfig.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class DictPatternValidatorTest {

    @Autowired
    Validator validator;

    static class Form {
        @DictPattern(dictType = "sys_normal_disable", separator = ",")
        String status;

        Form(String status) {
            this.status = status;
        }
    }

    @Test
    @DisplayName("字典内合法值通过校验")
    void validValue_passes() {
        Set<ConstraintViolation<Form>> violations = validator.validate(new Form("1"));
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("字典外的值校验失败,返回默认错误信息")
    void invalidValue_fails() {
        Set<ConstraintViolation<Form>> violations = validator.validate(new Form("99"));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("字典值无效");
    }

    @Test
    @DisplayName("空字符串校验失败 (validator 对 blank value 直接返回 false)")
    void blankValue_fails() {
        Set<ConstraintViolation<Form>> violations = validator.validate(new Form(""));
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("null 值校验失败")
    void nullValue_fails() {
        Set<ConstraintViolation<Form>> violations = validator.validate(new Form(null));
        assertThat(violations).hasSize(1);
    }

    @Configuration
    static class MockConfig {

        /**
         * 假的 DictService:仅 "1" / "2" 视为 sys_normal_disable 下合法字典值。
         */
        @Bean
        DictService dictService() {
            return new DictService() {
                @Override
                public String getDictLabel(String dictType, String dictValue, String separator) {
                    if (!"sys_normal_disable".equals(dictType)) {
                        return null;
                    }
                    return switch (dictValue) {
                        case "1" -> "正常";
                        case "2" -> "停用";
                        default -> null;
                    };
                }

                @Override
                public String getDictValue(String dictType, String dictLabel, String separator) {
                    return null;
                }

                @Override
                public Map<String, String> getAllDictByDictType(String dictType) {
                    return Map.of();
                }

                @Override
                public DictTypeDTO getDictType(String dictType) {
                    return null;
                }

                @Override
                public List<DictDataDTO> getDictData(String dictType) {
                    return List.of();
                }
            };
        }
    }
}
