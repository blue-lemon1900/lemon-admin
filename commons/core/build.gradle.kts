plugins {
    id("lemon.java-library-conventions")
}

description = "核心模块"

dependencies {
    // Servlet 依赖
    compileOnlyApi("jakarta.servlet:jakarta.servlet-api")

    // 编译期注解处理器，自动生成 getter/setter/构造器等样板代码
    compileOnlyApi("org.projectlombok:lombok")
    // Spring框架基本的核心工具
    compileOnly("org.springframework:spring-context-support")
    // SpringWeb模块
    compileOnlyApi("org.springframework:spring-web")

    // 自动生成YML配置关联JSON文件（annotationProcessor 由 java-library-conventions 统一声明）
    runtimeOnly("org.springframework.boot:spring-boot-properties-migrator")

    // Spring-AOP 相关依赖
    api("org.springframework:spring-aop")
    api("org.aspectj:aspectjweaver")

    // 参数校验相关依赖
    api("jakarta.validation:jakarta.validation-api")
    api("jakarta.annotation:jakarta.annotation-api")
    api("org.slf4j:slf4j-api")

    // 常用工具类
    api("org.apache.commons:commons-lang3")

    // Apache Commons IO：文件、流操作工具类
    api(libs.commons.io)

    // 离线IP地址定位库
    api(libs.ip2region)

    // hutool 工具库（版本由 BomCoordinates.HUTOOL 中的 hutool-bom 统一管理）
    api("cn.hutool:hutool-core")
    api("cn.hutool:hutool-http")
    api("cn.hutool:hutool-extra")

    // MapStruct：编译期生成对象映射代码（DTO <-> Entity 转换）
    api("io.github.linpeilie:mapstruct-plus-spring-boot-starter")

    // Netty 在 macOS 上有一个专门的原生 DNS 解析实现，能更准确地读取 macOS 系统的 DNS 配置。
    implementation("io.netty:netty-resolver-dns-native-macos") {
        artifact {
            classifier = "osx-aarch_64"
        }
    }
}
