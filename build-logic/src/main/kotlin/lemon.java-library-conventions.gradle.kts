import gradle.kotlin.dsl.accessors._63287c54d84282e5815fad8cda0586b7.annotationProcessor
import org.springframework.boot.gradle.plugin.SpringBootPlugin

// 约定插件：lemon.java-library-conventions
// 适用范围：commons/*, modules/* 下的公共库模块
plugins {
    // java-library 插件：适合库模块，相比 java 插件额外提供了 api / implementation 依赖分离
    // api：依赖会暴露给使用此库的模块（编译期可见）
    // implementation：依赖仅内部使用，不会泄漏给上层模块（推荐默认使用）
    `java-library`
}

// 统一 Maven 坐标
group = "org.lemon"
version = "0.0.1"

// 统一依赖仓库
repositories {
    mavenCentral()
}

// 统一 Java 工具链版本
// 工具链（toolchain）会自动检测或下载对应 JDK，无需手动配置 JAVA_HOME
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}

dependencies {
    // 注意：Gradle 中每种配置（implementation、annotationProcessor 等）
    // 是独立的依赖解析路径，彼此不继承 platform 约束。
    // 因此需要分别为每个用到的配置声明 platform()，确保所有依赖都能从 BOM 获取版本号。
    val springBom = platform(SpringBootPlugin.BOM_COORDINATES)
    val hutoolBom = platform(BomCoordinates.HUTOOL)
    val mybatisPlusBom = platform(BomCoordinates.MYBATIS_PLUS)
    val mapstructBom = platform(BomCoordinates.MAPSTRUCT)

    // 覆盖主代码的编译期和运行期依赖（compileClasspath / runtimeClasspath）
    implementation(springBom)
    implementation(hutoolBom)
    implementation(mybatisPlusBom)
    api(mapstructBom)

    // 覆盖注解处理器的独立解析路径
    annotationProcessor(springBom)
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor(BomCoordinates.MAPSTRUCT_PROCESSOR)

    // 覆盖测试代码的编译期和运行期依赖
    testImplementation(springBom)

    // 覆盖测试注解处理器的独立解析路径
    testAnnotationProcessor(springBom)
}
