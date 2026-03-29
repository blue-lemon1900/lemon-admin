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
}