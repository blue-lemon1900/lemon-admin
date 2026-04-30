plugins {
    java
    id("org.springframework.boot")
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
    // 同 java-library-conventions，需对每个独立的解析配置分别声明 platform()
    val springBom = platform(libs.findLibrary("spring-boot-bom").get())
    val hutoolBom = platform(libs.findLibrary("hutool-bom").get())
    val mybatisPlusBom = platform(libs.findLibrary("mybatis-plus-bom").get())

    // 覆盖主代码的编译期和运行期依赖（compileClasspath / runtimeClasspath）
    implementation(springBom)
    implementation(hutoolBom)
    implementation(mybatisPlusBom)

    // 应用启动类所需的基础依赖（@SpringBootApplication 等）
    implementation("org.springframework.boot:spring-boot-starter")

    // 覆盖注解处理器的独立解析路径
    annotationProcessor(springBom)
    annotationProcessor("org.projectlombok:lombok")

    // 覆盖测试代码的编译期和运行期依赖
    testImplementation(springBom)

    // 覆盖测试注解处理器的独立解析路径
    testAnnotationProcessor(springBom)
}
