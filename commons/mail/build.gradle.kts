plugins {
    // 应用公共库约定插件，自动获得：
    // java-library、BOM 版本管理、group/version、仓库、JDK 工具链
    id("lemon.java-library-conventions")
}

description = "邮件模块"

dependencies {
    implementation(project(":core"))

    implementation("jakarta.mail:jakarta.mail-api")
    implementation("org.eclipse.angus:jakarta.mail")
}