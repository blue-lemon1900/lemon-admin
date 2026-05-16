// 应用启动模块约定:在 lemon.java-base-conventions 基础上,
// 追加 Spring Boot 插件 + 启动类所需的 spring-boot-starter 依赖。
plugins {
    id("lemon.java-base-conventions")
    id("org.springframework.boot")
}

dependencies {
    // 应用启动类所需的基础依赖(@SpringBootApplication 等)
    implementation("org.springframework.boot:spring-boot-starter")
}
