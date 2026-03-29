import org.springframework.boot.gradle.plugin.SpringBootPlugin

// 约定插件：lemon.spring-boot-app-conventions
// 适用范围：apps/* 下的可执行 Spring Boot 应用模块
// 使用方式：在子模块的 build.gradle.kts 中声明 id("lemon.spring-boot-app-conventions")
plugins {
    java
    id("org.springframework.boot")
}

// 统一 Maven 坐标
group = "org.lemon"
version = "0.0.1"

// 统一依赖仓库
repositories {
    mavenCentral()
}

// 统一 Java 工具链版本
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}

dependencies {
    // 同 java-library-conventions，需对每个独立的解析配置分别声明 platform()
    val springBom = platform(SpringBootPlugin.BOM_COORDINATES)
    val hutoolBom = platform(BomCoordinates.HUTOOL)
    val mybatisPlusBom = platform(BomCoordinates.MYBATIS_PLUS)

    // 覆盖主代码的编译期和运行期依赖（compileClasspath / runtimeClasspath）
    implementation(springBom)
    implementation(hutoolBom)
    implementation(mybatisPlusBom)

    // 覆盖注解处理器的独立解析路径
    annotationProcessor(springBom)
    annotationProcessor("org.projectlombok:lombok")

    // 覆盖测试代码的编译期和运行期依赖
    testImplementation(springBom)

    // 覆盖测试注解处理器的独立解析路径
    testAnnotationProcessor(springBom)
}
