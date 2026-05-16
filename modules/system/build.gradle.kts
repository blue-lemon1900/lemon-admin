plugins {
    id("lemon.java-library-conventions")
}

description = "系统基础功能,业务逻辑模块"

dependencies {
    implementation(project(":core"))
    implementation(project(":system-api"))
    implementation(project(":validation"))
    implementation(project(":json"))
    implementation(project(":sensitive"))
    implementation(project(":oss"))
    implementation(project(":idempotent"))
    implementation(project(":ratelimiter"))
    implementation(project(":excel"))
    implementation(project(":log"))
    implementation(project(":translation"))
    implementation(project(":web"))
    implementation(project(":security"))
    implementation(project(":mybatis"))
    implementation(project(":redis"))
    implementation(project(":encrypt"))
    implementation(project(":sse"))
    implementation(project(":tenant"))

    // MapStruct:本模块 Vo/Bo 类使用 @AutoMapper 注解,需要 BaseMapper / annotation 类在 compileClasspath 上
    implementation("io.github.linpeilie:mapstruct-plus-spring-boot-starter")
}
