plugins {
    id("lemon.spring-boot-app-conventions")
}

description = "Web服务入口模块"

dependencies {
    implementation(project(":system"))

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
