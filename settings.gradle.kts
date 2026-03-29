// pluginManagement 必须是 settings.gradle.kts 的第一个块
pluginManagement {
    // includeBuild("build-logic") 将 build-logic 作为复合构建引入，
    // 使其中定义的约定插件（lemon.*）在所有子模块中可直接通过 id() 使用
    includeBuild("build-logic")
}

rootProject.name = "lemon-admin"

/* ---------------------------------- 公共库模块 ---------------------------------- */
include(":core")
project(":core").projectDir = file("commons/core")

include(":json")
project(":json").projectDir = file("commons/json")

include(":redis")
project(":redis").projectDir = file("commons/redis")

include(":security")
project(":security").projectDir = file("commons/security")

include(":web")
project(":web").projectDir = file("commons/web")

include(":mybatis")
project(":mybatis").projectDir = file("commons/mybatis")

include(":log")
project(":log").projectDir = file("commons/log")

include(":oss")
project(":oss").projectDir = file("commons/oss")

include(":sse")
project(":sse").projectDir = file("commons/sse")

include(":translation")
project(":translation").projectDir = file("commons/translation")

include(":ratelimiter")
project(":ratelimiter").projectDir = file("commons/ratelimiter")

include(":encrypt")
project(":encrypt").projectDir = file("commons/encrypt")

include(":sensitive")
project(":sensitive").projectDir = file("commons/sensitive")

include(":idempotent")
project(":idempotent").projectDir = file("commons/idempotent")

include(":tenant")
project(":tenant").projectDir = file("commons/tenant")

include(":excel")
project(":excel").projectDir = file("commons/excel")

/* ---------------------------------- 业务逻辑模块 ---------------------------------- */
include(":system")
project(":system").projectDir = file("modules/system")

/* ---------------------------------- 服务入口模块 ---------------------------------- */
include(":admin")
project(":admin").projectDir = file("apps/admin")