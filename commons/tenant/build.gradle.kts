plugins {
    id("lemon.java-library-conventions")
}

description = "多租户模块"

dependencies {
    implementation(project(":mybatis"))
    implementation(project(":redis"))
}

