plugins {
    id("lemon.java-library-conventions")
}

description = "核心模块"

dependencies {
    // Servlet：由 Servlet 容器在运行时提供
    compileOnlyApi("jakarta.servlet:jakarta.servlet-api")
    // Spring Web：仅在工具类签名中出现
    compileOnlyApi("org.springframework:spring-web")
    // Lombok：编译期注解
    compileOnlyApi("org.projectlombok:lombok")

    // Spring AOP
    api("org.springframework:spring-aop")
    api("org.aspectj:aspectjweaver")

    //  API 规范 jar（替代原 starter-validation 隐式带入的部分）
    api("jakarta.validation:jakarta.validation-api")
    api("jakarta.annotation:jakarta.annotation-api")
    api("org.slf4j:slf4j-api")

    // 通用工具类
    api("org.apache.commons:commons-lang3")
    api(libs.commons.io)
    // Hutool 工具库
    api("cn.hutool:hutool-core")
    api("cn.hutool:hutool-http")
    api("cn.hutool:hutool-extra")

    // 离线 IP 地址定位
    api(libs.ip2region)

    // MapStruct:本模块 MapstructUtils 工具类内部依赖 Converter,运行时由 Spring Boot autoconfig 注入。
    // 用 implementation 不向下游暴露,需要 @AutoMapper 编译期注解的模块(:security/:system)各自显式声明同一坐标。
    implementation("io.github.linpeilie:mapstruct-plus-spring-boot-starter")

    // 检测 application.yml 中已弃用的配置键并提示迁移
    runtimeOnly("org.springframework.boot:spring-boot-properties-migrator")
}
