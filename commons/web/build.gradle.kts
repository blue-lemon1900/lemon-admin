plugins {
    id("lemon.java-library-conventions")
}

description = "web服务模块"

dependencies {
    // JSON 序列化支持
    implementation(project(":json"))

    // Spring MVC：提供 REST 接口、Filter、拦截器等 Web 基础能力
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnlyApi("org.springframework.security:spring-security-web")

    // hutool 加解密工具（AES/RSA 等）
    implementation("cn.hutool:hutool-crypto")
    // hutool 图形验证码生成，api 向上游模块传递
    api("cn.hutool:hutool-captcha")
}