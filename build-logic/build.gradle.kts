// build-logic 自身的构建文件
plugins {
    // kotlin-dsl 插件让我们可以用 Kotlin 编写约定插件（.gradle.kts 文件）
    `kotlin-dsl`
}

dependencies {
    // 将 Spring Boot Gradle 插件加入 build-logic 的编译类路径。
    // 这样 src/main/kotlin/ 下的约定插件才能调用：
    // SpringBootPlugin.BOM_COORDINATES 常量 → 获取完整的 BOM 坐标字符串
    implementation(libs.spring.boot.gradle.plugin)
}
