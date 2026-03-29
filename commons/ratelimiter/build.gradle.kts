plugins {
    id("lemon.java-library-conventions")
}

description = "限流功能模块"

dependencies {
    implementation(project(":core"))
    implementation(project(":redis"))
}