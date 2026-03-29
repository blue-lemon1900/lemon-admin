plugins {
    id("lemon.java-library-conventions")
}

description = "数据加解密模块"

dependencies {
    implementation(project(":core"))

    // 加密包引入
    implementation(libs.bcprov.jdk15to18)
    implementation("cn.hutool:hutool-crypto")

    compileOnly("org.springframework:spring-webmvc")

    compileOnly("com.baomidou:mybatis-plus-spring-boot4-starter") {
        exclude(group = "org.mybatis", module = "mybatis-spring")
    }
}