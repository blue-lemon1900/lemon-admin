plugins {
    id("lemon.java-library-conventions")
}

description = "excel文件操作模块"

dependencies {
    implementation(project(":json"))
    api(libs.fastexcel)
}

