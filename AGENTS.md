# AGENTS.md

Behavioral guidelines to reduce common LLM coding mistakes. Merge with project-specific instructions as needed.

**Tradeoff:** These guidelines bias toward caution over speed. For trivial tasks, use judgment.

## 1. Think Before Coding

[](https://github.com/forrestchang/andrej-karpathy-skills/blob/main/CLAUDE.md#1-think-before-coding)

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:

* State your assumptions explicitly. If uncertain, ask.
* If multiple interpretations exist, present them - don't pick silently.
* If a simpler approach exists, say so. Push back when warranted.
* If something is unclear, stop. Name what's confusing. Ask.

## 2. Simplicity First

[](https://github.com/forrestchang/andrej-karpathy-skills/blob/main/CLAUDE.md#2-simplicity-first)

**Minimum code that solves the problem. Nothing speculative.**

* No features beyond what was asked.
* No abstractions for single-use code.
* No "flexibility" or "configurability" that wasn't requested.
* No error handling for impossible scenarios.
* If you write 200 lines and it could be 50, rewrite it.

Ask yourself: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

## 3. Surgical Changes

[](https://github.com/forrestchang/andrej-karpathy-skills/blob/main/CLAUDE.md#3-surgical-changes)

**Touch only what you must. Clean up only your own mess.**

When editing existing code:

* Don't "improve" adjacent code, comments, or formatting.
* Don't refactor things that aren't broken.
* Match existing style, even if you'd do it differently.
* If you notice unrelated dead code, mention it - don't delete it.

When your changes create orphans:

* Remove imports/variables/functions that YOUR changes made unused.
* Don't remove pre-existing dead code unless asked.

The test: Every changed line should trace directly to the user's request.

## 4. Goal-Driven Execution

[](https://github.com/forrestchang/andrej-karpathy-skills/blob/main/CLAUDE.md#4-goal-driven-execution)

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:

* "Add validation" → "Write tests for invalid inputs, then make them pass"
* "Fix the bug" → "Write a test that reproduces it, then make it pass"
* "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state a brief plan:

```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong success criteria let you loop independently. Weak criteria ("make it work") require constant clarification.

---

**These guidelines are working if:** fewer unnecessary changes in diffs, fewer rewrites due to overcomplication, and clarifying questions come before implementation rather than after mistakes.

## Project Name

LogiAgent｜基于 Spring C:contentReference[oaicite:0]{index=0}oal

本项目是一个用于实习简历展示的 Java 微服务 + AI Agent 项目。

目标不是做一个大而全的物流系统，而是做一个结构清晰、能跑通核心链路、能体现后端工程能力和 Agent 开发能力的项目。

核心闭环：

1. 物流业务闭环：下单 → 生成运单 → 更新轨迹 → 查询状态 → 异常诊断
2. 微服务闭环：Gateway → Nacos → OpenFeign → MySQL / Redis / MQ
3. Agent 闭环：用户问题 → 意图识别 → Tool Calling → 多服务查询 → 智能分析 → 报告生成

---

## User Background

项目开发者是 Java 后端方向学生，已有 Spring Boot、Spring Cloud、MySQL、Redis、Nacos、算法竞赛基础。

Codex 在开发时应优先选择：

- Java / Spring Boot / Spring Cloud Alibaba 技术栈
- 清晰可讲的后端架构
- 简历友好的模块设计
- 可运行、可演示、可解释的实现
- 不追求过度复杂，不堆砌无意义技术

---

## Tech Stack

默认技术栈如下：

- Java 17
- Spring Boot 3.x
- Spring Cloud Alibaba
- Nacos
- Spring Cloud Gateway
- OpenFeign
- Sentinel
- Seata，可后期加入
- MyBatis Plus
- MySQL 8
- Redis
- RabbitMQ，可后期加入
- Spring AI / Spring AI Alibaba
- Docker Compose
- Vue 3 + Element Plus，可后期加入

如果当前代码中尚未引入某项技术，不要强行一次性加入。优先保证 MVP 可运行。

---

## Repository Structure

推荐项目结构：

```text
logiagent
├── pom.xml
├── README.md
├── AGENTS.md
├── docker-compose.yml
├── docs
│   ├── 开发文档.md
│   ├── 接口文档.md
│   ├── 数据库设计.md
│   ├── 部署文档.md
│   └── knowledge
│       ├── 异常件处理规则.md
│       ├── 派件超时处理规范.md
│       └── 客服话术模板.md
├── logistics-common
├── logistics-api
├── logistics-gateway
├── logistics-auth-service
├── logistics-user-service
├── logistics-order-service
├── logistics-waybill-service
├── logistics-track-service
├── logistics-route-service
├── logistics-dispatch-service
└── logistics-ai-agent-service
```

Service Responsibilities
logistics-common

公共模块，包含：

统一返回 Result
全局异常 BusinessException
错误码 ErrorCode
常量类
工具类
基础 DTO / VO
通用分页对象

不要在 common 模块中放具体业务逻辑。

logistics-api

远程调用接口模块，包含：

Feign Client
DTO
Request / Response 对象
服务间共享接口

规则：

Feign 接口统一放在 logistics-api
不允许服务之间直接复制 DTO
不允许跨服务直接访问数据库
logistics-gateway

网关服务，负责：

路由转发
跨域配置
统一鉴权过滤
请求日志
黑白名单，可后期加入

网关不处理具体业务逻辑。

logistics-auth-service

认证服务，负责：

用户登录
JWT Token 签发
Token 校验
用户角色识别

MVP 阶段可以简化权限模型，只保留：

USER
COURIER
DRIVER
ADMIN
logistics-user-service

用户服务，负责：

用户信息
快递员信息
司机信息
管理员信息

用户服务只管理人员基础信息，不处理订单、运单、调度业务。

logistics-order-service

订单服务，负责：

创建寄件订单
查询订单
修改订单状态
取消订单

订单创建后，可以通过 Feign 或 MQ 通知运单服务生成运单。

MVP 阶段可以先用 Feign，同步生成运单；后期再改成 RabbitMQ 异步。

logistics-waybill-service

运单服务，负责：

生成运单
查询运单
更新运单状态
标记异常件
查询异常件

运单是物流系统核心对象。

常见状态：

CREATED
WAITING_COLLECT
COLLECTED
TRANSPORTING
ARRIVED_STATION
DELIVERING
SIGNED
CANCELLED
EXCEPTION
logistics-track-service

轨迹服务，负责：

新增物流轨迹
查询运单轨迹
查询最近轨迹
判断轨迹是否长时间未更新

轨迹数据用于 Agent 异常分析。

logistics-route-service

路线服务，负责：

网点管理
线路管理
路线规划
最短路径计算

路线规划应体现算法能力。

推荐实现：

Dijkstra 最短路
按距离最短
按时间最短
按成本最低
综合评分策略
logistics-dispatch-service

调度服务，负责：

派件任务
快递员任务
司机任务
网点负载统计
调度建议基础数据

MVP 阶段至少实现：

查询网点负载
创建派件任务
查询任务状态
logistics-ai-agent-service

AI Agent 服务，是项目核心亮点。

负责：

自然语言问答
意图识别
Tool Calling
运单异常诊断
物流日报生成
物流规则知识库问答
Agent 调用日志记录

重要规则：

AI Agent 服务不直接访问其他服务数据库。

正确链路：

Agent → Tool → Feign Client → 业务服务 → 数据库

错误链路：

Agent → 直接查 order-service 数据库
Agent → 直接查 waybill-service 数据库
Agent Design
Agent List

推荐设计以下 Agent：

SupervisorAgent
├── WaybillQueryAgent
├── TrackAnalysisAgent
├── RoutePlanningAgent
├── ExceptionDiagnosisAgent
├── DispatchSuggestAgent
├── LogisticsReportAgent
└── KnowledgeBaseAgent
SupervisorAgent

职责：

判断用户意图
路由到具体 Agent
管理多步骤调用流程

意图枚举：

public enum AgentIntent {
    WAYBILL_QUERY,
    WAYBILL_EXCEPTION_DIAGNOSIS,
    ROUTE_PLANNING,
    DISPATCH_SUGGESTION,
    DAILY_REPORT,
    KNOWLEDGE_QA,
    UNKNOWN
}
WaybillQueryAgent

处理问题：

“帮我查一下这个运单到哪了”
“WB202605090001 当前是什么状态”
“这个快递有没有签收”

需要调用：

WaybillTool
TrackTool
ExceptionDiagnosisAgent

处理问题：

“这个运单为什么还没签收”
“这个快递是不是异常了”
“为什么物流一直不更新”

需要调用：

WaybillTool
TrackTool
RouteTool
DispatchTool
KnowledgeTool

诊断逻辑：

查询运单基础信息
查询最近轨迹
判断预计到达时间是否超时
判断轨迹是否长时间未更新
查询当前网点负载
查询路线是否异常
生成异常原因和处理建议
RoutePlanningAgent

处理问题：

“广州到深圳怎么走最快”
“从 A 网点到 B 网点成本最低路线”
“帮我规划运输路线”

需要调用：

RouteTool

路线算法优先使用 Java 实现，不要完全依赖大模型。

DispatchSuggestAgent

处理问题：

“今天哪个网点压力最大”
“哪个快递员任务最多”
“怎么安排派件更合理”

需要调用：

DispatchTool
WaybillTool
TrackTool
LogisticsReportAgent

处理问题：

“生成今天物流日报”
“统计今天异常件”
“分析本周签收率”

输出格式建议为 Markdown。

报告内容包括：

总订单数
新增运单数
已签收数量
异常件数量
超时件数量
网点负载排行
Agent 分析结论
调度建议
KnowledgeBaseAgent

处理问题：

“拒收件怎么处理”
“运输超时怎么处理”
“派件失败应该怎么办”

知识来源：

docs/knowledge/异常件处理规则.md
docs/knowledge/派件超时处理规范.md
docs/knowledge/客服话术模板.md
Tool Calling Rules

Agent Tool 必须是可控的 Java 方法。

推荐工具：

WaybillTool
TrackTool
RouteTool
DispatchTool
OrderTool
UserTool
KnowledgeTool
ReportTool

Tool 命名规则：

类名使用 XxxTool
方法名表达明确业务含义
Tool 方法只做一件事
Tool 返回 DTO，不返回 Entity
Tool 内部通过 Feign 调用业务服务
Tool 必须记录调用日志

示例：

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
Database Design Rules

数据库命名：

表名使用 t_xxx
主键统一使用 id BIGINT
创建时间 create_time
更新时间 update_time
逻辑删除字段 deleted，可后期加入

核心表：

t_user
t_order
t_waybill
t_track
t_station
t_route
t_dispatch_task
t_agent_session
t_agent_tool_log

Entity、DTO、VO 区分：

Entity：数据库映射对象，只在 service 内部使用
DTO：服务间传输对象
VO：返回给前端的展示对象
Request：前端请求参数对象

不允许 Controller 直接返回 Entity。

API Design Rules

统一返回格式：

public class Result<T> {
    private Integer code;
    private String message;
    private T data;
}

接口路径规则：

/api/orders
/api/waybills
/api/tracks
/api/routes
/api/dispatch
/api/agent

Controller 规则：

只做参数接收和返回
不写复杂业务逻辑
复杂逻辑放到 Service
跨服务调用放到 Feign Client
参数必须校验
Coding Style

Java 代码规范：

使用构造器注入，避免字段注入
类名语义明确
方法不要过长
单个方法尽量不超过 80 行
不要写无意义注释
复杂业务逻辑必须写注释
不要吞异常
不要在 Controller 中写业务逻辑
不要在 Service 中返回 Map
不要使用魔法值，使用枚举或常量

推荐包结构：

controller
service
service.impl
mapper
entity
dto
vo
request
enums
constant
config
client
Microservice Rules

服务间调用：

同步调用使用 OpenFeign
异步事件后期使用 RabbitMQ
不允许一个服务直接访问另一个服务的数据库
不允许循环依赖调用
公共 DTO 放 logistics-api
公共工具放 logistics-common

服务拆分原则：

先保证 MVP 能跑
不为拆而拆
不要一开始引入过多复杂组件
先实现业务，再优化架构
MVP Priority

Codex 开发时优先完成 MVP。

MVP 必须包含：

logistics-common
logistics-api
logistics-gateway
logistics-auth-service
logistics-order-service
logistics-waybill-service
logistics-track-service
logistics-ai-agent-service

MVP 必须跑通：

用户登录
创建订单
生成运单
添加轨迹
查询运单
AI 查询运单
AI 分析异常
AI 生成简易日报

以下功能可以后期再做：

Sentinel
Seata
Elasticsearch
SkyWalking
完整 Vue 前端
复杂权限系统
复杂调度算法
MCP
多模型切换
Development Workflow For Codex

每次开发新功能时，Codex 应遵循以下流程：

先阅读 README.md、AGENTS.md、docs 目录
确认当前模块结构
给出简短实现计划
修改代码
补充必要测试或启动说明
更新相关文档
总结本次改动

不要在没有理解项目结构的情况下大规模改动。

Commands

如果是 Maven 多模块项目，优先使用：

mvn clean package -DskipTests

运行单个服务：

mvn spring-boot:run -pl logistics-gateway
mvn spring-boot:run -pl logistics-auth-service
mvn spring-boot:run -pl logistics-order-service
mvn spring-boot:run -pl logistics-waybill-service
mvn spring-boot:run -pl logistics-track-service
mvn spring-boot:run -pl logistics-ai-agent-service

运行测试：

mvn test

如果测试暂时缺失，不要伪造测试结果。应明确说明：

当前项目尚未补充测试，本次仅完成编译检查。
Documentation Rules

每完成一个核心功能，Codex 应同步更新文档。

至少维护：

README.md
docs/开发文档.md
docs/接口文档.md
docs/数据库设计.md
docs/部署文档.md

README.md 应包含：

项目介绍
技术栈
模块说明
启动步骤
核心功能
Agent 能力
项目截图，可后期加入
简历描述，可后期加入

接口文档应包含：

请求路径
请求方法
请求参数
返回示例
Security Rules

禁止：

在代码中硬编码 API Key
在代码中硬编码数据库密码
提交 .env
提交真实 Token
提交真实手机号、地址、身份证号
让 Agent 直接执行危险 SQL
让 Agent 直接删除业务数据

配置应使用：

application.yml
bootstrap.yml
Nacos Config
.env.example

API Key 示例：

spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
Agent Safety Rules

Agent 只能提供建议，不能直接执行高风险操作。

低风险操作：

查询运单
查询轨迹
查询网点负载
生成日报
分析异常原因

高风险操作：

删除订单
取消运单
修改派件任务
修改司机任务
执行赔付
批量更新状态

高风险操作必须由人工确认，MVP 阶段不要实现自动执行。

Example User Questions

Agent 应支持以下问题：

帮我查询运单 WB202605090001 到哪了
这个运单为什么还没有签收
帮我分析今天异常件最多的网点
广州到深圳哪个运输路线最快
生成今天的物流日报
拒收件应该怎么处理
运输超时应该怎么处理