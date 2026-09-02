# 用户订单服务

一个用于学习 Spring Boot 后端开发的用户订单服务。项目提供用户创建、订单创建、库存扣减和订单查询接口，并覆盖参数校验、统一异常处理、事务、日志、接口文档与测试隔离等实践。

## 技术栈

- Java 21
- Spring Boot 4
- MyBatis
- MySQL 8+
- Maven Wrapper
- JUnit 5 / Mockito
- Springdoc OpenAPI / Swagger UI

## 项目结构

```text
src/main/java/com/wpcc/userorderservice
├── common        # 统一错误响应、异常处理、配置
├── health        # 健康检查接口
├── user          # 用户 Controller、Service、DTO、Mapper
├── product       # 商品 Mapper 与库存业务
├── order         # 订单 Controller、Service、DTO、Mapper
└── jdbc          # JDBC 学习示例
```

职责分层：

```text
Controller → Service → Mapper → MySQL
```

- Controller：处理 HTTP 请求、状态码和请求 DTO。
- Service：承载业务规则、事务与库存校验。
- Mapper：仅负责 SQL 与数据库对象映射。
- DTO：定义 API 请求和响应，不直接暴露数据库对象。

## 环境要求

- JDK 21
- MySQL 8+
- 不需要单独安装 Maven，项目包含 Maven Wrapper。

## 数据库初始化

主库建表脚本：

```text
sql/schema.sql
```

测试库建表和初始化测试数据：

```text
sql/create-database-test.sql
```

在 MySQL 客户端中执行测试库脚本：

```sql
SOURCE D:/code/java/user-order-service/sql/create-database-test.sql;
```

## 本地配置与启动

复制配置模板，创建仅供本机使用的文件：

```text
src/main/resources/application-local.example.yml
→ src/main/resources/application-local.yml
```

本地配置只引用环境变量：

```yml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

请在终端或 VS Code 启动配置中设置 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD`，不要把真实密码写入仓库。

启动应用：

```powershell
.\mvnw.cmd spring-boot:run
```

默认地址：`http://localhost:8080`

## API 文档

启动后访问：

- Swagger UI：`http://localhost:8080/swagger-ui.html`
- OpenAPI JSON：`http://localhost:8080/v3/api-docs`

核心接口：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/users` | 创建用户 |
| GET | `/api/users/{id}` | 按 ID 查询用户 |
| POST | `/api/orders` | 创建订单并扣减库存 |
| GET | `/api/orders/{id}` | 按 ID 查询订单 |

创建用户示例：

```json
{
  "username": "alice"
}
```

创建订单示例：

```json
{
  "userId": 1,
  "productId": 1,
  "quantity": 2
}
```

## 测试

运行全部测试：

```powershell
.\mvnw.cmd test
```

普通单元测试和 Web 测试不连接真实数据库。数据库集成测试仅在配置以下环境变量后运行：

```text
TEST_DB_URL
TEST_DB_USERNAME
TEST_DB_PASSWORD
```

它们使用 `integration` profile 和独立的 `user_order_service_test` 数据库，避免误操作本地开发库。

## 项目特性

- 使用 `@Valid` 校验请求参数，并返回字段级错误信息。
- 使用 `@RestControllerAdvice` 统一处理 400、404、409 等错误。
- 订单创建处于事务中：扣库存、保存订单、保存订单详情要么全部成功，要么全部回滚。
- 使用条件更新 `stock >= quantity` 防止并发下库存扣成负数。
- 记录订单创建、库存不足和异常关键路径日志，不打印密码、Token 等敏感信息。
- 使用 OpenAPI 自动生成交互式接口文档。
