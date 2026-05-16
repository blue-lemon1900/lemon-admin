// Library 模块约定:在 lemon.java-base-conventions 基础上,
// 追加 java-library 插件、MapStruct(api 暴露给下游)、Spring Boot 配置处理器、JUnit Platform。
plugins {
    id("lemon.java-base-conventions")
    `java-library`
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    val mapstructPom = platform(libs.findLibrary("mapstruct-plus-pom").get())
    implementation(mapstructPom)

    // library 特有的注解处理器
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor(libs.findLibrary("mapstruct-plus-processor").get())

    // Gradle 9.x 要求显式声明 JUnit Platform launcher
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
