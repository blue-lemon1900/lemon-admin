plugins {
    id("lemon.java-library-conventions")
}

description = "SSE模块"

dependencies {
    implementation(project(":json"))
    implementation(project(":redis"))
    implementation(project(":security"))
}

