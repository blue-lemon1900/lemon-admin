plugins {
    id("lemon.java-library-conventions")
}

description = "数据库服务"

dependencies {
    compileOnlyApi(project(":security"))

    // Mybatis-Plus 自动自动装配
    api("com.baomidou:mybatis-plus-spring-boot4-starter")
    // MyBatis-Plus 的分页插件
    api("com.baomidou:mybatis-plus-jsqlparser")
    // dynamic-datasource 多数据源
    implementation(libs.dynamic.datasource)
    // sql性能分析插件
    implementation(libs.p6spy)
}