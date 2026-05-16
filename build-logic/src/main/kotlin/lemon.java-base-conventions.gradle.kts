// 所有 JVM 模块(library + app)共享的基础约定
plugins {
    java
}

group = "org.lemon"
version = "0.0.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}

// 通过 VersionCatalogsExtension 运行时 API 访问根项目共享的 libs catalog。
// precompiled script plugin 不支持 libs.xxx 类型安全访问器（gradle/gradle#15383）。
val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    // 是独立的依赖解析路径,彼此不继承 platform 约束。
    // 因此需要分别为每个用到的配置声明 platform(),确保所有依赖都能从 BOM 获取版本号。
    val springBom = platform(libs.findLibrary("spring-boot-bom").get())
    val hutoolBom = platform(libs.findLibrary("hutool-bom").get())
    val mybatisPlusBom = platform(libs.findLibrary("mybatis-plus-bom").get())

    // 覆盖主代码的编译期和运行期依赖(compileClasspath / runtimeClasspath)
    implementation(springBom)
    implementation(hutoolBom)
    implementation(mybatisPlusBom)

    // Spring Boot 基础:本仓库所有 library/app 模块都是 Spring Boot 组件,
    // 直接使用 @AutoConfiguration、@Bean、@ConditionalOn*、@ConfigurationProperties 等注解。
    // 显式声明,避免靠任何一条 api 链路 transitive leak。
    implementation("org.springframework.boot:spring-boot-starter")

    // 覆盖注解处理器的独立解析路径
    annotationProcessor(springBom)
    annotationProcessor("org.projectlombok:lombok")

    // 覆盖测试代码的编译期和运行期依赖
    testImplementation(springBom)

    // 覆盖测试注解处理器的独立解析路径
    testAnnotationProcessor(springBom)
}
