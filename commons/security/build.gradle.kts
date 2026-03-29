plugins {
    id("lemon.java-library-conventions")
}

description = "json 身份验证和访问控制模块"

dependencies {
    compileOnlyApi(project(":redis"))
    compileOnlyApi(project(":json"))

    compileOnlyApi("org.springframework:spring-webmvc")
    api("org.springframework.boot:spring-boot-starter-security")

    // google 工具类
    implementation(libs.guava)
    // 解决 ThreadLocal 父子线程的传值问题
    implementation(libs.transmittable.thread.local)
}