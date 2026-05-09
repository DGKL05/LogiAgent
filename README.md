# LogiAgent

LogiAgent 是一个 Java 17 + Spring Cloud Alibaba 物流微服务项目，后续会把 AI Agent 作为核心展示亮点。

当前处于 Milestone 2：物流核心业务闭环。已完成订单、运单、轨迹三个核心服务的基础 API、MyBatis Plus 接入、OpenFeign 同步生成运单、Gateway 路由与 SQL 初始化脚本。

## 技术栈

- Java 17
- Spring Boot 3.5.14
- Spring Cloud 2025.0.2
- Spring Cloud Alibaba 2025.0.0.0
- Spring Cloud Gateway
- OpenFeign
- Nacos
- MyBatis Plus
- MySQL 8
- Maven 多模块

## 模块说明

```text
logiagent
├── logistics-common              通用 Result、ErrorCode、BusinessException、枚举、分页对象
├── logistics-api                 服务间 DTO、Request、Feign Client
├── logistics-gateway             Gateway 路由
├── logistics-auth-service        认证服务骨架
├── logistics-order-service       订单服务
├── logistics-waybill-service     运单服务
├── logistics-track-service       轨迹服务
└── logistics-ai-agent-service    AI Agent 服务骨架
```

## 本阶段核心链路

```text
创建订单 -> OpenFeign 调用运单服务生成运单 -> 查询运单 -> 添加轨迹 -> 查询轨迹 -> 标记异常件
```

AI Agent、路线规划、调度、MQ 异步化暂未实现。

## 本地启动

### 1. 启动依赖

```bash
docker compose up -d mysql nacos
```

Nacos 控制台：

```text
http://localhost:8848/nacos
```

MySQL 默认数据库为 `logiagent`。本项目不提交真实密码，`docker-compose.yml` 会优先读取系统环境变量 `MySQLPASS`。

### 2. 初始化数据库

```bash
mysql -h 127.0.0.1 -P 3306 -u root -p%MySQLPASS% < docs/sql/init-logistics-core.sql
```

PowerShell 可以使用管道执行：

```powershell
$env:MYSQL_PWD=$env:MySQLPASS
Get-Content -Raw docs/sql/init-logistics-core.sql | mysql -h 127.0.0.1 -P 3306 -u root
```

也可以先进入 MySQL 客户端后执行：

```sql
source docs/sql/init-logistics-core.sql;
```

### 3. 编译

```bash
mvn clean package -DskipTests
```

### 4. 启动服务

```bash
mvn spring-boot:run -pl logistics-gateway
mvn spring-boot:run -pl logistics-order-service
mvn spring-boot:run -pl logistics-waybill-service
mvn spring-boot:run -pl logistics-track-service
```

服务端口：

| 服务 | 端口 |
| --- | ---: |
| logistics-gateway | 8080 |
| logistics-auth-service | 9001 |
| logistics-order-service | 9002 |
| logistics-waybill-service | 9003 |
| logistics-track-service | 9004 |
| logistics-ai-agent-service | 9005 |

## Gateway 路由

| 路径 | 目标服务 |
| --- | --- |
| `/api/auth/**` | `lb://logistics-auth-service` |
| `/api/orders/**` | `lb://logistics-order-service` |
| `/api/waybills/**` | `lb://logistics-waybill-service` |
| `/api/tracks/**` | `lb://logistics-track-service` |
| `/api/agent/**` | `lb://logistics-ai-agent-service` |

## 快速验证链路

创建订单：

```bash
curl -X POST http://localhost:8080/api/orders ^
  -H "Content-Type: application/json" ^
  -d "{\"senderId\":1,\"receiverName\":\"Zhang San\",\"receiverPhone\":\"13800000000\",\"receiverAddress\":\"Shenzhen Nanshan\",\"goodsName\":\"electronics\",\"weight\":2.50}"
```

根据返回的 `waybillNo` 添加轨迹：

```bash
curl -X POST http://localhost:8080/api/tracks ^
  -H "Content-Type: application/json" ^
  -d "{\"waybillNo\":\"WB202605090001\",\"action\":\"COLLECTED\",\"description\":\"Courier collected the parcel\",\"operatorId\":10}"
```

标记异常件：

```bash
curl -X POST http://localhost:8080/api/waybills/WB202605090001/exception ^
  -H "Content-Type: application/json" ^
  -d "{\"exceptionType\":\"TIMEOUT\",\"exceptionReason\":\"No update for a long time\"}"
```

## 注意事项

- 不要提交真实 API Key、Token、数据库密码、手机号、地址、身份证号。
- AI Agent 后续只能通过 Tool + Feign 调用业务服务，不能直接访问其他服务数据库。
- 当前阶段仅完成核心物流业务 MVP，不包含登录、权限、Agent、路线规划、调度、MQ。
