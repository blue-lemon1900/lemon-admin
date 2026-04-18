import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    java
    id("org.springframework.boot")
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

dependencies {
    // 同 java-library-conventions，需对每个独立的解析配置分别声明 platform()
    val springBom = platform(SpringBootPlugin.BOM_COORDINATES)
    val hutoolBom = platform(BomCoordinates.HUTOOL)
    val mybatisPlusBom = platform(BomCoordinates.MYBATIS_PLUS)

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
