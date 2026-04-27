package org.lemon.commons.mybatis.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MyBatis-Plus 项目级扩展配置
 * <p>
 * 与官方 {@code com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties} 共用
 * {@code mybatis-plus} 前缀;Spring Boot 允许多个 @ConfigurationProperties 类绑定同一前缀,
 * 各自只挑自己声明的字段,运行时互不干扰。
 */
@Data
@ConfigurationProperties(prefix = "mybatis-plus")
public class MybatisPlusExtensionProperties {

    /**
     * 是否全局开启逻辑删除;关闭后所有 @TableLogic 失效
     */
    private Boolean enableLogicDelete = true;

    /**
     * Mapper 接口扫描包路径,支持 ** 通配,例如 org.lemon.**.mapper
     */
    private String mapperPackage;
}
