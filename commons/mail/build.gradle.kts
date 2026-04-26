plugins {
    id("lemon.java-library-conventions")
}

description = "邮件模块"

dependencies {
    implementation(project(":core"))

    implementation("jakarta.mail:jakarta.mail-api")
    implementation("org.eclipse.angus:jakarta.mail")
}