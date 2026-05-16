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
    // MapStruct BOM 通过 api() 暴露,让下游消费者也对齐 MapStruct 版本
    val mapstructBom = platform(libs.findLibrary("mapstruct-plus-bom").get())
    api(mapstructBom)

    // library 特有的注解处理器
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor(libs.findLibrary("mapstruct-plus-processor").get())

    // Gradle 9.x 要求显式声明 JUnit Platform launcher
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
