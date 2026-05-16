// 应用启动模块约定:在 lemon.java-base-conventions 基础上,
// 追加 Spring Boot 插件 + 启动类所需的 spring-boot-starter 依赖。
plugins {
    id("lemon.java-base-conventions")
    id("org.springframework.boot")
}
