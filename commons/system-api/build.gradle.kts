plugins {
    id("lemon.java-library-conventions")
}

description = "系统域服务契约（接口 + DTO + 字典验证器）"

dependencies {
    // DictPatternValidator 通过 SpringUtils 反查 DictService Bean；
    // 验证注解依赖 jakarta.validation（由 :core 经 spring-boot-starter-validation api 暴露）。
    api(project(":core"))
}
