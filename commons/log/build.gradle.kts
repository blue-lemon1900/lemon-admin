plugins {
    id("lemon.java-library-conventions")
}

description = "日志记录模块"

dependencies {
    implementation(project(":json"))
    implementation(project(":security"))
}