plugins {
    id("lemon.java-library-conventions")
}

description = "参数校验模块"

dependencies {
    api(project(":core"))
    api(project(":system-api"))

    api("org.springframework.boot:spring-boot-starter-validation")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}