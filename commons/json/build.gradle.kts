plugins {
    id("lemon.java-library-conventions")
}

description = "序列化模块"

dependencies {
    api(project(":core"))
    api("org.springframework.boot:spring-boot-jackson")
    implementation("tools.jackson.core:jackson-databind")
}