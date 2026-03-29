/**
 * 统一管理各第三方 BOM 的坐标字符串。
 * 版本号需与 gradle/libs.versions.toml 中保持一致。
 *
 * 原因：在 Gradle 9 的约定插件（precompiled script plugin）中，
 * `libs` 类型安全访问器和 `VersionCatalogsExtension` 均不可用，
 * 因此采用与 SpringBootPlugin.BOM_COORDINATES 相同的常量模式。
 */
object BomCoordinates {
    const val HUTOOL = "cn.hutool:hutool-bom:5.8.44"
    const val MYBATIS_PLUS = "com.baomidou:mybatis-plus-bom:3.5.16"
    const val MAPSTRUCT = "io.github.linpeilie:mapstruct-plus-pom:1.5.0"
    const val MAPSTRUCT_PROCESSOR = "io.github.linpeilie:mapstruct-plus-processor:1.5.0"
}
