plugins {
    id("lemon.java-library-conventions")
}

description = "缓存服务"

dependencies {
    implementation(project(":core"))

    api(libs.redisson)
    api(libs.lock4j)
    implementation(libs.redisson.cache)

    compileOnly("tools.jackson.core:jackson-databind")
    implementation("com.github.ben-manes.caffeine:caffeine")

    // 一个极速多语言序列化框架，基于 JIT 编译和零拷贝技术
    // implementation("org.apache.fory:fory-core:0.15.0")
}