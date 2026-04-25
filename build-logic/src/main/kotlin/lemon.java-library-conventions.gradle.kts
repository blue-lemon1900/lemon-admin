plugins {
    `java-library`
}

group = "org.lemon"
version = "0.0.1"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

// 通过 VersionCatalogsExtension 运行时 API 访问根项目共享的 libs catalog。
// precompiled script plugin 不支持 libs.xxx 类型安全访问器（gradle/gradle#15383）。
val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    // 注意：Gradle 中每种配置（implementation、annotationProcessor 等）
    // 是独立的依赖解析路径，彼此不继承 platform 约束。
    // 因此需要分别为每个用到的配置声明 platform()，确保所有依赖都能从 BOM 获取版本号。
    val springBom = platform(libs.findLibrary("spring-boot-bom").get())
    val hutoolBom = platform(libs.findLibrary("hutool-bom").get())
    val mybatisPlusBom = platform(libs.findLibrary("mybatis-plus-bom").get())
    val mapstructBom = platform(libs.findLibrary("mapstruct-plus-bom").get())

    // 覆盖主代码的编译期和运行期依赖（compileClasspath / runtimeClasspath）
    implementation(springBom)
    implementation(hutoolBom)
    implementation(mybatisPlusBom)
    api(mapstructBom)

    // 覆盖注解处理器的独立解析路径
    annotationProcessor(springBom)
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor(libs.findLibrary("mapstruct-plus-processor").get())

    // 覆盖测试代码的编译期和运行期依赖
    testImplementation(springBom)
    // Gradle 9.x 要求显式声明 JUnit Platform launcher
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // 覆盖测试注解处理器的独立解析路径
    testAnnotationProcessor(springBom)
}
