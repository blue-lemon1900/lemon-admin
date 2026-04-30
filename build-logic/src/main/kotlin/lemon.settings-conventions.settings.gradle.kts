// Settings 约定插件:集中声明所有子项目共享的依赖仓库。
// Gradle 9.5 起 settings 预编译脚本插件也支持类型安全 Kotlin 访问器,
// 因此 dependencyResolutionManagement {} 在 IDE 里有自动补全和编译期检查。

dependencyResolutionManagement {
    // FAIL_ON_PROJECT_REPOS:子项目里再写 repositories { ... } 直接构建失败,
    // 强制所有依赖仓库只能在此处统一声明。
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS

    repositories {
        mavenCentral()
    }
}
