rootProject.name = "build-logic"

dependencyResolutionManagement {

    repositories {
        // kotlin-dsl 插件及其依赖需要从 Gradle 插件门户解析
        gradlePluginPortal()
        mavenCentral()
    }

    // 将根项目的版本目录共享给 build-logic，
    // 这样约定插件中可以通过 libs.versions.xxx 读取统一的版本号
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
