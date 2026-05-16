plugins {
    id("lemon.java-library-conventions")
}

description = "json 身份验证和访问控制模块"

dependencies {
    compileOnlyApi(project(":redis"))
    compileOnlyApi(project(":json"))

    compileOnlyApi("org.springframework:spring-webmvc")
    api("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // google 工具类
    implementation(libs.guava)
    // ContextPropagatingTaskDecorator 的运行时依赖
    implementation("io.micrometer:context-propagation")

    // MapStruct:本模块 VO 类使用 @AutoMapper 注解,需要 BaseMapper / annotation 类在 compileClasspath 上
    implementation("io.github.linpeilie:mapstruct-plus-spring-boot-starter")
}