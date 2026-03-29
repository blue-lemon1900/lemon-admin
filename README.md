# Lemon Admin

基于 Spring Boot 4.0.3 + Java 25 的多模块后台管理系统脚手架，采用 Gradle 约定插件统一构建规范，支持多租户、分布式缓存、接口加密、SSE 推送等企业级特性。

## 技术栈

| 技术 | 版本 |
|------|------|
| Spring Boot | 4.0.3 |
| Java | 25 |
| MyBatis-Plus | 3.5.16 |
| Redisson | 4.3.0 |
| HuTool | 5.8.44 |
| FastExcel | 1.3.0 |
| AWS SDK (S3) | 2.28.22 |

## 项目结构

```
lemon-admin
├── build-logic/          # Gradle 约定插件（统一构建规范）
├── commons/              # 公共基础库模块
│   ├── core/             # 核心工具：通用工具类、基础注解、IP 定位
│   ├── json/             # 序列化：Jackson 扩展配置
│   ├── redis/            # 缓存：Redisson 封装、分布式锁
│   ├── security/         # 安全：Token 认证与访问控制
│   ├── web/              # Web：拦截器、验证码、XSS 防护
│   ├── mybatis/          # 数据库：MyBatis-Plus 扩展、多数据源、p6spy
│   ├── log/              # 日志：操作日志记录
│   ├── oss/              # 对象存储：AWS S3 兼容接口
│   ├── sse/              # 服务端推送：SSE 长连接
│   ├── encrypt/          # 加解密：字段加密、API 请求加密
│   ├── sensitive/        # 数据脱敏：手机号、身份证等字段脱敏
│   ├── idempotent/       # 幂等：基于 Redis 的重复请求拦截
│   ├── ratelimiter/      # 限流：接口限流控制
│   ├── translation/      # 翻译：字典值自动翻译
│   ├── tenant/           # 多租户：租户隔离与数据过滤
│   └── excel/            # Excel：FastExcel 文件导入导出
├── modules/
│   └── system/           # 系统业务模块：用户、角色、菜单、部门等
└── apps/
    └── admin/            # 应用入口：Spring Boot 启动模块
```

## 系统功能

- **用户权限**：用户、角色、菜单、部门、岗位管理
- **系统配置**：参数配置、字典管理、通知公告
- **租户管理**：多租户隔离、租户套餐配置
- **对象存储**：OSS 配置管理、文件上传
- **社交登录**：第三方社交账号绑定
- **SSE 推送**：服务端实时消息推送

## 快速开始

```bash
# 构建
./gradlew build

# 启动
./gradlew bootRun

# 测试
./gradlew test

# 运行指定测试类
./gradlew test --tests "org.lemon.admin.SomeTest"
```

服务启动后访问：`http://localhost:8080`

## 配置说明

核心配置文件：`apps/admin/src/main/resources/application.yml`

| 配置项 | 说明 |
|--------|------|
| `server.port` | 服务端口，默认 `8080` |
| `spring.profiles.active` | 激活环境，默认 `dev` |
| `tenant.enable` | 是否开启多租户，默认 `true` |
| `lemon.security.token-header` | Token 请求头名称 |
| `lemon.security.permit-all-urls` | 免认证白名单 |
| `api-decrypt.enabled` | 是否开启接口加密，默认 `true` |
| `xss.enabled` | 是否开启 XSS 防护，默认 `true` |
| `sse.enabled` | 是否开启 SSE 推送，默认 `true` |
| `logging.file.path` | 日志输出目录 |
