import org.springframework.boot.gradle.plugin.SpringBootPlugin

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
    // Gradle 9.x 要求显式声明 JUnit Platform launcher
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // 覆盖测试注解处理器的独立解析路径
    testAnnotationProcessor(springBom)
}
