# LogiAgent

LogiAgent 是一个 Java 17 + Spring Cloud Alibaba 物流微服务项目，AI Agent 是当前阶段的核心展示能力。

当前处于 Milestone 3：AI Agent MVP。已完成订单、运单、轨迹核心业务闭环，并新增规则版 Agent：用户用自然语言询问运单，Agent 识别意图后通过 Tool + Feign 调用业务服务，汇总运单和轨迹数据并返回自然语言分析。

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
├── logistics-common              Result、ErrorCode、BusinessException、枚举、分页对象
├── logistics-api                 服务间 DTO、Request、Feign Client
├── logistics-gateway             Gateway 路由
├── logistics-auth-service        认证服务骨架
├── logistics-order-service       订单服务
├── logistics-waybill-service     运单服务
├── logistics-track-service       轨迹服务
└── logistics-ai-agent-service    规则版 AI Agent MVP
```

## 核心链路

物流业务：

```text
创建订单 -> 生成运单 -> 添加轨迹 -> 查询运单 -> 查询轨迹 -> 标记异常件
```

Agent 业务：

```text
用户问题 -> 意图识别 -> WaybillTool / TrackTool -> Feign 调用业务服务 -> 规则诊断 -> 保存会话和 Tool 日志
```

## Agent MVP 能力

当前是规则版 Agent，不强制调用真实大模型。

支持问题示例：

- `帮我分析运单 WB20260509204042075 为什么还没签收`
- `查询一下 WB20260509204042075 到哪里了`
- `这个运单是不是异常了`

意图识别规则：

- 包含“为什么”“异常”“没签收”“不更新”“分析”等关键词时，识别为 `WAYBILL_EXCEPTION_DIAGNOSIS`。
- 包含 `WB...` 运单号但不包含异常诊断关键词时，识别为 `WAYBILL_QUERY`。

AI Agent 不直接访问 order、waybill、track 数据库，只通过 `WaybillTool`、`TrackTool` 调用 `logistics-api` 中的 Feign Client。

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

PowerShell：

```powershell
$env:MYSQL_PWD=$env:MySQLPASS
Get-Content -Raw docs/sql/init-logistics-core.sql | mysql -h 127.0.0.1 -P 3306 -u root
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
mvn spring-boot:run -pl logistics-ai-agent-service
```

## Gateway 路由

| 路径 | 目标服务 |
| --- | --- |
| `/api/auth/**` | `lb://logistics-auth-service` |
| `/api/orders/**` | `lb://logistics-order-service` |
| `/api/waybills/**` | `lb://logistics-waybill-service` |
| `/api/tracks/**` | `lb://logistics-track-service` |
| `/api/agent/**` | `lb://logistics-ai-agent-service` |

## Agent 验证

```bash
curl -X POST http://localhost:8080/api/agent/chat ^
  -H "Content-Type: application/json" ^
  -d "{\"userId\":1,\"message\":\"帮我分析运单 WB20260509204042075 为什么还没签收\"}"
```

返回的 `data` 包含：

- `sessionId`
- `intent`
- `answer`
- `toolCalls`

可通过 MySQL 验证日志：

```sql
SELECT * FROM t_agent_session ORDER BY create_time DESC LIMIT 5;
SELECT * FROM t_agent_tool_log ORDER BY create_time DESC LIMIT 10;
```

## 注意事项

- 不要提交真实 API Key、Token、数据库密码、手机号、地址、身份证号。
- `spring.ai.openai.api-key=${OPENAI_API_KEY:}` 仅作为后续大模型接入占位。
- 当前不包含前端、路线规划、调度、RabbitMQ、Sentinel、Seata、复杂 RAG。
