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

    // 直接依赖 application.yml 里配置的所有模块
    implementation(project(":core"))
    implementation(project(":encrypt"))
    implementation(project(":web"))
    implementation(project(":sse"))
    implementation(project(":security"))
    implementation(project(":tenant"))
    implementation(project(":mail"))
    implementation(project(":mybatis"))
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
