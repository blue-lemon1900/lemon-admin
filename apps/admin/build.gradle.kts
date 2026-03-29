plugins {
    id("lemon.spring-boot-app-conventions")
}

description = "Web服务入口模块"

dependencies {
    implementation(project(":system"))
    implementation(project(":web"))
    implementation(project(":security"))
    implementation(project(":redis"))
    implementation(project(":encrypt"))
    implementation(project(":sse"))
    implementation(project(":tenant"))

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
