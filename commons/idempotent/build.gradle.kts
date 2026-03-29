plugins {
    id("lemon.java-library-conventions")
}

description = "幂等功能"

dependencies {
    implementation(project(":redis"))
    implementation(project(":security"))
    implementation("cn.hutool:hutool-crypto")
}