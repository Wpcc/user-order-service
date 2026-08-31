# 第 11 周复盘：JDBC 与 MyBatis

## 1. JDBC 与 MyBatis 的分工

JDBC 需要开发者自己建立数据库连接、创建 `PreparedStatement`、设置参数、遍历 `ResultSet`，并在最后关闭资源和处理 SQL 异常。

MyBatis 在 JDBC 的基础上封装了连接、参数绑定和结果映射等重复工作。开发者主要编写 SQL、声明 Mapper 方法与结果对象，就能完成数据库读写。

## 2. Mapper 的职责

`UserMapper`、`ProductMapper` 和 `OrderMapper` 分别负责用户、商品、订单相关表的数据库访问，例如新增、按 id 查询和列表查询。

Mapper 只处理持久层读写，不处理 HTTP 请求，也不承载复杂业务规则。Controller 接收和返回 HTTP 数据，Service 组织业务流程并调用 Mapper，这样各层职责更清晰。

## 3. 参数与结果映射

`@Param` 为 Mapper 方法参数指定名称，使 SQL 可以通过 `#{userId}`、`#{status}` 等占位符安全引用对应参数。

查询结果的列名会和 `DatabaseOrder` 等 record 的组件名匹配；项目开启了下划线转驼峰配置，因此 `user_id` 可以映射为 `userId`。

查询单条且结果可能不存在时使用 `Optional`；查询多条时使用 `List`，没有匹配数据时返回空列表。

## 4. 动态 SQL 与安全性

`<if>` 用于决定一个查询条件是否加入 SQL；`<where>` 自动添加 `WHERE` 并清理多余的开头 `AND`；`<choose>` 类似 Java 的 `switch/case`，从多个固定分支中选择一个。

排序字段属于 SQL 结构，不能直接使用前端传入的字符串。项目用 `OrderSortField` 枚举建立白名单，并用 `<choose>` 输出固定字段名，避免 SQL 注入和非法字段错误。

## 5. 测试与配置

单元测试不连接真实数据库，专注验证单个类的逻辑，例如 `OrderPageRequest` 的页码校验和 `offset()` 计算。它执行快、定位问题直接。

数据库集成测试会启动 Spring 并连接真实数据库，用来验证 SQL、Mapper 参数绑定和查询结果映射是否正确。它依赖 `local` profile 与 `DB_URL`，使本地数据库配置和普通测试环境隔离。

数据库密码不能提交到 Git：公开仓库或协作者都可能读取提交历史中的密钥，造成数据库被未授权访问。密码应保留在本地环境变量或被忽略的配置文件中。
