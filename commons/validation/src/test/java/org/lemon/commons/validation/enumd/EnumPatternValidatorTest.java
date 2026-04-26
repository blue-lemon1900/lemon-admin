package org.lemon.commons.validation.enumd;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * EnumPattern + EnumPatternValidator 单元测试。
 * 不依赖 Spring 上下文：直接使用 Hibernate Validator 默认 ValidatorFactory。
 */
class EnumPatternValidatorTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    enum SexEnum {
        MALE("1"),
        FEMALE("2"),
        UNKNOWN("0");

        private final String code;

        SexEnum(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    static class Form {
        @EnumPattern(type = SexEnum.class, fieldName = "code")
        String sex;

        Form(String sex) {
            this.sex = sex;
        }
    }

    @Test
    @DisplayName("枚举内合法值通过校验")
    void validValue_passes() {
        Set<ConstraintViolation<Form>> violations = validator.validate(new Form("1"));
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("枚举外的值校验失败,返回默认错误信息")
    void invalidValue_fails() {
        Set<ConstraintViolation<Form>> violations = validator.validate(new Form("9"));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("输入值不在枚举范围内");
    }

    @Test
    @DisplayName("空字符串校验失败 (StringUtils.isNotBlank 返回 false)")
    void blankValue_fails() {
        Set<ConstraintViolation<Form>> violations = validator.validate(new Form(""));
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("null 值校验失败 (与 @NotNull 不重合,该实现不会放行 null)")
    void nullValue_fails() {
        Set<ConstraintViolation<Form>> violations = validator.validate(new Form(null));
        assertThat(violations).hasSize(1);
    }
}
