plugins {
    id("lemon.spring-boot-app-conventions")
}

description = "Web服务入口模块"

dependencies {
    implementation(project(":system"))

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // macOS 原生 DNS 解析，让 Netty 准确读取系统 DNS 配置
    implementation("io.netty:netty-resolver-dns-native-macos") {
        artifact {
            classifier = "osx-aarch_64"
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
