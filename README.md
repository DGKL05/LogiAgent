# LogiAgent

> 基于 Spring Cloud Alibaba 的智能物流调度 Agent 系统

LogiAgent 是一个面向物流业务场景的 **Java 微服务 + AI Agent** 项目。项目以物流系统中的订单、运单、轨迹、路线、调度等核心业务为基础，引入 AI Agent 实现自然语言查询、异常件诊断、路线规划建议和物流日报生成。

本项目目标不是做一个大而全的物流平台，而是打造一个结构清晰、可运行、可演示、适合写进简历的后端项目。

---

## 项目定位

传统物流系统通常需要人工在多个后台页面中查询订单、运单、物流轨迹、网点负载和调度任务。LogiAgent 希望通过 AI Agent 将这些操作整合成自然语言交互。

例如：

```text
帮我查询运单 WB202605090001 到哪了
这个运单为什么还没有签收？
今天哪个网点异常件最多？
广州到深圳走哪条路线最快？
生成今天的物流日报
```

系统会自动识别用户意图，调用对应业务服务，聚合多服务数据，并返回分析结果。

---

## 核心亮点

- 基于 Spring Cloud Alibaba 构建微服务架构
- 使用 Nacos 实现服务注册与配置管理
- 使用 Gateway 作为统一 API 入口
- 使用 OpenFeign 完成服务间远程调用
- 设计订单、运单、轨迹、路线、调度等物流核心业务模型
- 引入 Spring AI / Spring AI Alibaba 构建 AI Agent 服务
- 通过 Tool Calling 封装业务工具能力
- 支持自然语言运单查询、异常件诊断、物流日报生成
- 将物流网点和线路抽象为图结构，支持路线规划算法扩展
- 预留 RAG 知识库能力，用于物流规则、异常件处理规范问答

---

## 技术栈

### 后端


| 技术                          | 说明                     |
| ----------------------------- | ------------------------ |
| Java 17                       | 主开发语言               |
| Spring Boot 3.x               | 微服务基础框架           |
| Spring Cloud Alibaba          | 微服务治理               |
| Nacos                         | 注册中心、配置中心       |
| Spring Cloud Gateway          | API 网关                 |
| OpenFeign                     | 服务间远程调用           |
| MyBatis Plus                  | ORM 框架                 |
| MySQL 8                       | 关系型数据库             |
| Redis                         | 缓存、登录态、热点数据   |
| RabbitMQ                      | 异步消息，后期扩展       |
| Spring AI / Spring AI Alibaba | Agent、Tool Calling、RAG |
| Docker Compose                | 本地开发环境编排         |

### 前端，后期扩展


| 技术         | 说明        |
| ------------ | ----------- |
| Vue 3        | 前端框架    |
| TypeScript   | 类型约束    |
| Element Plus | 后台管理 UI |
| ECharts      | 数据可视化  |
| Axios        | HTTP 请求   |

---

## 系统架构

```text
                    用户 / 客服 / 调度员 / 管理员
                              |
                              v
                       Web 前端 / Chat UI
                              |
                              v
                     logistics-gateway
                              |
        -------------------------------------------------
        |          |          |          |              |
        v          v          v          v              v
 auth-service  user-service order-service waybill-service track-service
                                                   |
                                                   v
                                           route-service
                                                   |
                                                   v
                                        dispatch-service
                                                   |
                                                   v
                                      ai-agent-service
                                                   |
       -----------------------------------------------------------------
       |                 |                  |                          |
       v                 v                  v                          v
     MySQL             Redis              Vector DB                日志系统
```

---

## 项目模块

```text
logiagent
├── logistics-common              公共模块
├── logistics-api                 Feign 接口与 DTO 模块
├── logistics-gateway             网关服务
├── logistics-auth-service        认证授权服务
├── logistics-user-service        用户服务
├── logistics-order-service       订单服务
├── logistics-waybill-service     运单服务
├── logistics-track-service       轨迹服务
├── logistics-route-service       路线服务
├── logistics-dispatch-service    调度服务
└── logistics-ai-agent-service    AI Agent 服务
```

---

## 模块说明

### logistics-common

公共基础模块，包含：

- 统一返回对象 `Result`
- 全局异常 `BusinessException`
- 错误码 `ErrorCode`
- 常量类
- 工具类
- 通用分页对象

### logistics-api

服务间接口模块，包含：

- Feign Client
- DTO
- Request / Response 对象
- 服务间共享模型

服务之间不直接访问彼此数据库，只通过 Feign 或消息队列通信。

### logistics-gateway

统一入口服务，负责：

- 路由转发
- 跨域配置
- 鉴权过滤
- 请求日志
- 黑白名单扩展

### logistics-auth-service

认证服务，负责：

- 用户登录
- JWT Token 签发
- Token 校验
- 用户角色识别

MVP 阶段角色设计：

```text
USER      普通用户
COURIER   快递员
DRIVER    司机
ADMIN     管理员
```

### logistics-order-service

订单服务，负责：

- 创建寄件订单
- 查询订单详情
- 修改订单状态
- 取消订单

### logistics-waybill-service

运单服务，负责：

- 生成运单
- 查询运单
- 更新运单状态
- 标记异常件
- 查询异常件列表

运单核心状态：

```text
CREATED
WAITING_COLLECT
COLLECTED
TRANSPORTING
ARRIVED_STATION
DELIVERING
SIGNED
CANCELLED
EXCEPTION
```

### logistics-track-service

轨迹服务，负责：

- 新增物流轨迹
- 查询运单轨迹
- 查询最近轨迹
- 判断轨迹是否长时间未更新

### logistics-route-service

路线服务，负责：

- 网点管理
- 线路管理
- 路线规划
- 最短路径计算

路线规划支持后期扩展：

- 距离最短
- 时间最短
- 成本最低
- 综合最优

### logistics-dispatch-service

调度服务，负责：

- 派件任务
- 快递员任务
- 司机任务
- 网点负载统计
- 基础调度建议

### logistics-ai-agent-service

AI Agent 服务，负责：

- 自然语言问答
- 用户意图识别
- Tool Calling
- 运单异常诊断
- 物流日报生成
- 物流规则知识库问答
- Agent 会话与工具调用日志记录

Agent 服务不直接访问其他服务数据库。

正确链路：

```text
Agent -> Tool -> Feign Client -> 业务服务 -> 数据库
```

---

## AI Agent 设计

### Agent 角色

```text
SupervisorAgent
├── WaybillQueryAgent
├── TrackAnalysisAgent
├── RoutePlanningAgent
├── ExceptionDiagnosisAgent
├── DispatchSuggestAgent
├── LogisticsReportAgent
└── KnowledgeBaseAgent
```


| Agent                   | 职责                           |
| ----------------------- | ------------------------------ |
| SupervisorAgent         | 识别用户意图，路由到具体 Agent |
| WaybillQueryAgent       | 查询运单、订单、轨迹信息       |
| TrackAnalysisAgent      | 分析物流轨迹是否异常           |
| RoutePlanningAgent      | 规划运输路线                   |
| ExceptionDiagnosisAgent | 分析异常件原因                 |
| DispatchSuggestAgent    | 生成调度建议                   |
| LogisticsReportAgent    | 生成物流日报、异常报告         |
| KnowledgeBaseAgent      | 基于物流知识库进行问答         |

### 意图类型

```java
public enum AgentIntent {
    WAYBILL_QUERY,
    WAYBILL_EXCEPTION_DIAGNOSIS,
    ROUTE_PLANNING,
    DISPATCH_SUGGESTION,
    DAILY_REPORT,
    KNOWLEDGE_QA,
    UNKNOWN
}
```

### Tool 设计

Agent 通过 Tool 调用业务服务，常见工具包括：


| Tool          | 调用服务         | 功能                   |
| ------------- | ---------------- | ---------------------- |
| WaybillTool   | waybill-service  | 查询运单详情           |
| TrackTool     | track-service    | 查询物流轨迹           |
| RouteTool     | route-service    | 查询路线、计算路线     |
| DispatchTool  | dispatch-service | 查询网点负载、派件任务 |
| OrderTool     | order-service    | 查询订单信息           |
| KnowledgeTool | RAG 模块         | 查询物流规则知识库     |
| ReportTool    | ai-agent-service | 生成 Markdown 报告     |

示例：

```java
@Component
public class WaybillTool {

    private final WaybillFeignClient waybillFeignClient;

    public WaybillTool(WaybillFeignClient waybillFeignClient) {
        this.waybillFeignClient = waybillFeignClient;
    }

    @Tool(description = "根据运单号查询运单详情")
    public WaybillDTO getWaybillByNo(String waybillNo) {
        return waybillFeignClient.getByWaybillNo(waybillNo).getData();
    }
}
```

---

## 核心业务流程

### 1. 用户寄件流程

```text
用户填写寄件信息
      |
      v
创建订单
      |
      v
生成运单
      |
      v
分配起始网点
      |
      v
生成揽收任务
      |
      v
快递员揽收
      |
      v
轨迹更新
```

### 2. 运单流转流程

```text
已下单
  ↓
待揽收
  ↓
已揽收
  ↓
运输中
  ↓
到达分拨中心
  ↓
派送中
  ↓
已签收
```

### 3. 异常件诊断流程

```text
输入运单号
  ↓
查询运单基础信息
  ↓
查询最近物流轨迹
  ↓
判断是否超过预计到达时间
  ↓
查询当前网点负载
  ↓
查询运输路线信息
  ↓
生成异常原因
  ↓
给出处理建议
```

---

## 路线规划设计

物流网点和线路可以抽象为图结构：

```text
网点 = 图的节点
线路 = 图的边
距离 / 时间 / 成本 = 边权
```

路线规划策略：


| 策略           | 说明     |
| -------------- | -------- |
| DISTANCE_FIRST | 距离最短 |
| TIME_FIRST     | 时间最短 |
| COST_FIRST     | 成本最低 |
| BALANCED       | 综合最优 |

综合评分示例：

```text
score = distance * 0.4 + estimatedTime * 0.4 + cost * 0.2
```

后期可以加入网点负载、天气、车辆状态等因素。

---

## 数据库设计

核心表：

```text
t_user
t_order
t_waybill
t_track
t_station
t_route
t_dispatch_task
t_agent_session
t_agent_tool_log
```

### t_order


| 字段             | 说明         |
| ---------------- | ------------ |
| id               | 主键         |
| order_no         | 订单号       |
| sender_id        | 寄件人 ID    |
| receiver_name    | 收件人姓名   |
| receiver_phone   | 收件人手机号 |
| receiver_address | 收件地址     |
| goods_name       | 物品名称     |
| weight           | 重量         |
| status           | 订单状态     |
| create_time      | 创建时间     |
| update_time      | 更新时间     |

### t_waybill


| 字段                 | 说明         |
| -------------------- | ------------ |
| id                   | 主键         |
| waybill_no           | 运单号       |
| order_no             | 订单号       |
| current_node_id      | 当前节点     |
| current_status       | 当前状态     |
| exception_type       | 异常类型     |
| expected_arrive_time | 预计到达时间 |
| actual_arrive_time   | 实际到达时间 |
| create_time          | 创建时间     |
| update_time          | 更新时间     |

### t_track


| 字段        | 说明     |
| ----------- | -------- |
| id          | 主键     |
| waybill_no  | 运单号   |
| node_id     | 当前节点 |
| action      | 轨迹动作 |
| description | 轨迹描述 |
| operator_id | 操作人   |
| event_time  | 事件时间 |
| create_time | 创建时间 |

### t_agent_session


| 字段        | 说明       |
| ----------- | ---------- |
| id          | 主键       |
| session_id  | 会话 ID    |
| user_id     | 用户 ID    |
| question    | 用户问题   |
| intent      | 识别意图   |
| answer      | Agent 回答 |
| create_time | 创建时间   |

### t_agent_tool_log


| 字段            | 说明     |
| --------------- | -------- |
| id              | 主键     |
| session_id      | 会话 ID  |
| tool_name       | 工具名称 |
| request_params  | 请求参数 |
| response_result | 响应结果 |
| success         | 是否成功 |
| error_msg       | 错误信息 |
| cost_ms         | 耗时     |
| create_time     | 创建时间 |

---

## 接口示例

### 创建订单

```http
POST /api/orders
```

请求示例：

```json
{
  "senderId": 1,
  "receiverName": "张三",
  "receiverPhone": "13800000000",
  "receiverAddress": "广东省深圳市南山区科技园",
  "goodsName": "电子产品",
  "weight": 2.5
}
```

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderNo": "OD202605090001"
  }
}
```

### 查询运单

```http
GET /api/waybills/{waybillNo}
```

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "waybillNo": "WB202605090001",
    "orderNo": "OD202605090001",
    "currentStatus": "TRANSPORTING",
    "currentNodeId": 1001,
    "exceptionType": null,
    "expectedArriveTime": "2026-05-10 18:00:00"
  }
}
```

### 查询物流轨迹

```http
GET /api/tracks/waybill/{waybillNo}
```

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "action": "COLLECTED",
      "description": "快递员已揽收",
      "eventTime": "2026-05-09 10:00:00"
    },
    {
      "action": "ARRIVED",
      "description": "快件已到达广州分拨中心",
      "eventTime": "2026-05-09 15:00:00"
    }
  ]
}
```

### AI Agent 问答

```http
POST /api/agent/chat
```

请求示例：

```json
{
  "userId": 1,
  "message": "帮我分析运单 WB202605090001 为什么还没签收"
}
```

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sessionId": "AGENT202605090001",
    "intent": "WAYBILL_EXCEPTION_DIAGNOSIS",
    "answer": "该运单当前停留在广州分拨中心，最近一次轨迹更新时间为 2026-05-09 15:00。结合预计到达时间和网点负载情况，初步判断为分拨中心负载较高导致中转延迟，建议优先安排下一班运输车辆。",
    "toolCalls": [
      "WaybillTool.getWaybillByNo",
      "TrackTool.getTracksByWaybillNo",
      "DispatchTool.getStationLoad"
    ]
  }
}
```

---

## 本地启动

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8+
- Redis 6+
- Nacos 2.x
- Docker / Docker Compose，可选

### 1. 克隆项目

```bash
git clone https://github.com/your-username/logiagent.git
cd logiagent
```

### 2. 初始化数据库

创建数据库：

```sql
CREATE DATABASE logiagent DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

导入 SQL：

```bash
mysql -u root -p logiagent < docs/sql/init.sql
```

### 3. 配置环境变量

复制环境变量模板：

```bash
cp .env.example .env
```

示例：

```env
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_USERNAME=root
MYSQL_PASSWORD=123456

REDIS_HOST=localhost
REDIS_PORT=6379

NACOS_SERVER_ADDR=localhost:8848

OPENAI_API_KEY=your_api_key
```

### 4. 启动基础组件

如果项目提供 Docker Compose：

```bash
docker compose up -d
```

或者自行启动：

- MySQL
- Redis
- Nacos

### 5. 编译项目

```bash
mvn clean package -DskipTests
```

### 6. 启动服务

```bash
mvn spring-boot:run -pl logistics-gateway
mvn spring-boot:run -pl logistics-auth-service
mvn spring-boot:run -pl logistics-order-service
mvn spring-boot:run -pl logistics-waybill-service
mvn spring-boot:run -pl logistics-track-service
mvn spring-boot:run -pl logistics-ai-agent-service
```

### 7. 访问服务

```text
Gateway: http://localhost:8080
Nacos:   http://localhost:8848/nacos
```

---

## 开发计划

### 第一阶段：微服务基础骨架

- [ ]  创建 Maven 父工程
- [ ]  创建 common、api、gateway 等基础模块
- [ ]  接入 Nacos
- [ ]  配置 Gateway 路由
- [ ]  实现统一返回与全局异常处理
- [ ]  实现基础登录认证

### 第二阶段：物流核心业务

- [ ]  创建订单
- [ ]  生成运单
- [ ]  添加物流轨迹
- [ ]  查询运单详情
- [ ]  查询轨迹列表
- [ ]  标记异常件

### 第三阶段：AI Agent 最小闭环

- [ ]  接入 Spring AI
- [ ]  实现 `/api/agent/chat`
- [ ]  实现 WaybillTool
- [ ]  实现 TrackTool
- [ ]  实现简单意图识别
- [ ]  实现运单查询 Agent
- [ ]  实现异常诊断 Agent
- [ ]  保存 Agent 会话日志

### 第四阶段：算法与调度能力

- [ ]  实现网点管理
- [ ]  实现线路管理
- [ ]  实现 Dijkstra 路线规划
- [ ]  实现网点负载统计
- [ ]  实现调度建议

### 第五阶段：RAG 与报告生成

- [ ]  编写物流规则知识库
- [ ]  实现文档切分与向量化
- [ ]  实现知识库问答
- [ ]  实现物流日报生成
- [ ]  输出 Markdown 分析报告

### 第六阶段：前端与部署

- [ ]  Vue 3 + Element Plus 后台页面
- [ ]  Agent 聊天页面
- [ ]  运单管理页面
- [ ]  轨迹查询页面
- [ ]  Docker Compose 一键启动
- [ ]  完善 README 和接口文档

---

## 项目演示场景

### 场景 1：自然语言查询运单

用户输入：

```text
帮我查一下 WB202605090001 到哪了
```

Agent 执行：

```text
识别意图 -> 调用 WaybillTool -> 调用 TrackTool -> 汇总状态 -> 返回结果
```

### 场景 2：异常件诊断

用户输入：

```text
这个运单为什么还没签收？
```

Agent 执行：

```text
查询运单 -> 查询轨迹 -> 判断是否超时 -> 查询网点负载 -> 生成异常原因和建议
```

### 场景 3：路线规划

用户输入：

```text
广州到深圳哪条运输路线最快？
```

系统执行：

```text
查询网点 -> 查询线路 -> 执行最短路算法 -> 返回路线方案
```

### 场景 4：物流日报

用户输入：

```text
生成今天的物流日报
```

Agent 输出：

```text
总订单数
新增运单数
已签收数量
异常件数量
超时件数量
网点负载排行
调度建议
```

---

## 简历描述参考

```text
LogiAgent｜基于 Spring Cloud Alibaba 的智能物流调度 Agent 系统

基于 Spring Cloud Alibaba 构建物流微服务系统，拆分网关、认证、订单、运单、轨迹、路线、调度、AI Agent 等服务模块；使用 Nacos 实现服务注册与配置管理，使用 Gateway 统一路由请求，使用 OpenFeign 完成服务间远程调用；引入 Spring AI 构建智能体服务，通过 Tool Calling 封装运单查询、轨迹查询、路线规划、网点负载查询等工具能力，实现自然语言运单查询、异常件诊断和物流日报生成；将物流网点与运输线路抽象为图结构，使用 Dijkstra 算法实现最短路径规划。
```

---

## 项目规范

### 分层规范

```text
controller   接收请求，参数校验
service      业务逻辑
mapper       数据访问
entity       数据库实体
dto          服务间传输对象
vo           前端展示对象
request      请求参数对象
enums        枚举
config       配置类
client       Feign 客户端
```

### 微服务规范

- 服务之间通过 OpenFeign 或消息队列通信
- 不允许跨服务直接访问数据库
- 不允许 Controller 直接操作 Mapper
- 不允许直接返回 Entity 给前端
- 公共 DTO 放在 `logistics-api`
- 公共工具放在 `logistics-common`

### Agent 规范

- Agent 不直接查数据库
- Agent 通过 Tool 调用业务服务
- Tool 返回 DTO，不返回 Entity
- 高风险操作必须人工确认
- Agent 调用过程需要记录日志

---

## 安全说明

项目中不要提交以下内容：

- 真实 API Key
- 数据库真实密码
- 真实 Token
- 真实手机号、地址、身份证号
- `.env` 文件
- 生产环境配置

推荐提交：

```text
.env.example
application-example.yml
```

---

## 当前状态

项目处于设计与开发阶段，当前优先完成 MVP：

```text
创建订单 -> 生成运单 -> 添加轨迹 -> 查询运单 -> AI 异常诊断
```

后续会逐步完善路线规划、调度建议、RAG 知识库、前端页面和 Docker 部署。

---

## License

本项目仅用于学习、交流和实习项目展示。

如需开源协议，可根据实际情况选择 MIT / Apache-2.0。
