plugins {
    id("lemon.java-library-conventions")
}

description = "OSS 服务"

dependencies {
    implementation(project(":json"))
    implementation(project(":redis"))

    // AWS SDK for Java 2.x - S3
    implementation(libs.awssdk.s3) {
        // 东西 30M 特别大的 jar 包，性能跟 Netty 差不多，有需要可以自行替换使用
        exclude(group = "software.amazon.awssdk", module = "aws-crt-client")
        // 将基于 Apache 的 HTTP 客户端从类路径中移除
        exclude(group = "software.amazon.awssdk", module = "apache-client")
        // 将配置基于 URL 连接的 HTTP 客户端从类路径中移除
        exclude(group = "software.amazon.awssdk", module = "url-connection-client")
    }
    // 适用于 Netty 的客户端
    implementation(libs.awssdk.netty.nio.client)
    // 客户端的性能增强传输管理器
    implementation(libs.awssdk.s3.transfer.manager)
}

