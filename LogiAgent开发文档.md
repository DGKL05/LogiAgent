# LogiAgent 开发文档 v1.0

项目名称：

<pre class="overflow-visible! px-0!" data-start="30" data-end="92"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>LogiAgent｜基于 Spring Cloud Alibaba 的智能物流调度 Agent 系统</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

项目定位：

> 一个面向物流业务场景的微服务系统，支持用户下单、运单管理、物流轨迹、网点路线、异常件诊断、调度建议，并引入 AI Agent 实现自然语言查询、异常原因分析、路线规划建议和物流日报生成。

---

# 1. 项目背景

传统物流系统通常只能通过后台页面查询订单、运单、轨迹和网点信息。对于客服、调度员、运维人员来说，如果想知道：

<pre class="overflow-visible! px-0!" data-start="270" data-end="355"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>这个运单为什么超时？</span><br/><span>今天哪个网点异常件最多？</span><br/><span>某个区域的派件压力是否过高？</span><br/><span>从广州到深圳走哪条线路更合适？</span><br/><span>最近系统接口异常主要集中在哪些服务？</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

往往需要人工查询多个系统，包括订单系统、轨迹系统、网点系统、司机系统、日志系统等。

LogiAgent 的目标是将传统物流系统升级为：

<pre class="overflow-visible! px-0!" data-start="427" data-end="464"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>物流微服务系统 + AI Agent 智能调度助手</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

用户可以通过自然语言提出问题，Agent 自动拆解任务，调用不同微服务接口，最后给出分析结果和调度建议。

---

# 2. 项目核心亮点

这个项目适合写进简历的原因是，它不是普通 CRUD 项目，而是结合了：


| 能力                | 说明                                         |
| ------------------- | -------------------------------------------- |
| Spring Cloud 微服务 | 网关、注册中心、配置中心、远程调用、服务治理 |
| 物流业务场景        | 订单、运单、轨迹、路线、网点、司机、异常件   |
| AI Agent            | 自然语言理解、任务拆解、工具调用、多步骤推理 |
| RAG                 | 物流规则、异常件处理手册、客服知识库问答     |
| 算法能力            | 路线规划、最短路、网点负载评估、调度策略     |
| 工程能力            | Docker 部署、日志监控、接口文档、权限认证    |

Spring Cloud Alibaba 官方介绍中包含 Nacos 服务注册与发现、Nacos 分布式配置、Sentinel 流控降级、Seata 分布式事务等能力，适合作为本项目的微服务技术基础。

Spring AI 的 Tool Calling 机制强调：模型只负责请求工具调用，真正执行工具的是应用程序本身，这对后端项目非常重要，因为你可以把“查询运单”“查询轨迹”“计算路线”等能力封装成安全可控的 Java 方法。

Spring AI Alibaba 当前也明确定位为面向 Java 开发者的 Agentic AI 框架，并支持 Agent、Workflow、Multi-Agent 等应用构建。

---

# 3. 总体架构

## 3.1 系统架构图

<pre class="overflow-visible! px-0!" data-start="1265" data-end="2704"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>                    用户 / 客服 / 调度员 / 管理员</span><br/><span>                              |</span><br/><span>                              v</span><br/><span>                        Web 前端 / Chat UI</span><br/><span>                              |</span><br/><span>                              v</span><br/><span>                     logistics-gateway</span><br/><span>                              |</span><br/><span>       ------------------------------------------------</span><br/><span>       |          |          |          |             |</span><br/><span>       v          v          v          v             v</span><br/><span>  auth-service user-service order-service waybill-service track-service</span><br/><span>                                                    |</span><br/><span>                                                    v</span><br/><span>                                           route-service</span><br/><span>                                                    |</span><br/><span>                                                    v</span><br/><span>                                         dispatch-service</span><br/><span>                                                    |</span><br/><span>                                                    v</span><br/><span>                                      ai-agent-service</span><br/><span>                                                    |</span><br/><span>        ----------------------------------------------------------------</span><br/><span>        |                 |                  |                         |</span><br/><span>        v                 v                  v                         v</span><br/><span>  MySQL / Redis      Elasticsearch       Vector DB / Milvus      日志系统</span><br/><span>        |</span><br/><span>        v</span><br/><span>      Nacos / Sentinel / Seata / RabbitMQ / Docker</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

## 3.2 请求链路示例

用户输入：

<pre class="overflow-visible! px-0!" data-start="2728" data-end="2773"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>帮我分析一下运单 WB202605090001 为什么还没有签收？</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

系统执行流程：

<pre class="overflow-visible! px-0!" data-start="2784" data-end="3030"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>1. 前端将问题发送到 ai-agent-service</span><br/><span>2. SupervisorAgent 识别用户意图：异常运单诊断</span><br/><span>3. 调用 WaybillTool 查询运单基础信息</span><br/><span>4. 调用 TrackTool 查询物流轨迹</span><br/><span>5. 调用 RouteTool 查询线路信息</span><br/><span>6. 调用 DispatchTool 查询网点和司机状态</span><br/><span>7. ExceptionDiagnosisAgent 分析异常原因</span><br/><span>8. ReportAgent 生成自然语言解释</span><br/><span>9. 返回诊断结果和建议</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

# 4. 技术选型

## 4.1 后端技术栈


| 技术                          | 用途                           |
| ----------------------------- | ------------------------------ |
| Java 17                       | 主开发语言                     |
| Spring Boot 3.x               | 微服务基础框架                 |
| Spring Cloud Alibaba          | 微服务治理                     |
| Nacos                         | 注册中心、配置中心             |
| Gateway                       | API 网关                       |
| OpenFeign                     | 服务间调用                     |
| Sentinel                      | 限流、熔断、降级               |
| Seata                         | 分布式事务                     |
| MyBatis Plus                  | ORM                            |
| MySQL                         | 核心业务数据库                 |
| Redis                         | 缓存、验证码、登录态、热点数据 |
| RabbitMQ / RocketMQ           | 异步消息                       |
| Elasticsearch                 | 运单、轨迹、日志搜索           |
| Spring AI / Spring AI Alibaba | Agent、Tool Calling、RAG       |
| Docker Compose                | 本地部署环境                   |

Nacos 官方定位是动态服务发现、配置管理和服务管理平台，其动态配置能力可以避免每次修改配置都重新部署服务。

Spring AI 提供跨模型能力，包括 Tool / Function Calling、可观测性、文档注入 ETL 等能力，适合做 AI 应用的 Java 后端集成。

## 4.2 前端技术栈


| 技术          | 用途                |
| ------------- | ------------------- |
| Vue 3         | 前端框架            |
| TypeScript    | 类型约束            |
| Element Plus  | 后台管理 UI         |
| Axios         | 请求后端接口        |
| ECharts       | 物流统计图表        |
| Markdown 渲染 | 展示 Agent 分析报告 |

---

# 5. 微服务模块设计

建议你先做精简版，不要一开始做太大。

## 5.1 服务拆分

<pre class="overflow-visible! px-0!" data-start="3939" data-end="4422"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>logiagent-parent</span><br/><span>├── logistics-gateway              网关服务</span><br/><span>├── logistics-auth-service         认证授权服务</span><br/><span>├── logistics-user-service         用户服务</span><br/><span>├── logistics-order-service        订单服务</span><br/><span>├── logistics-waybill-service      运单服务</span><br/><span>├── logistics-track-service        轨迹服务</span><br/><span>├── logistics-route-service        路线服务</span><br/><span>├── logistics-dispatch-service     调度服务</span><br/><span>├── logistics-ai-agent-service     AI Agent 服务</span><br/><span>├── logistics-common               公共模块</span><br/><span>└── logistics-api                  Feign 接口模块</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

## 5.2 服务职责


| 服务                       | 职责                             |
| -------------------------- | -------------------------------- |
| logistics-gateway          | 统一入口、路由转发、鉴权过滤     |
| logistics-auth-service     | 登录、注册、JWT、权限校验        |
| logistics-user-service     | 用户、快递员、司机、管理员信息   |
| logistics-order-service    | 寄件订单、订单状态、支付状态     |
| logistics-waybill-service  | 运单生成、运单状态、异常件标记   |
| logistics-track-service    | 物流轨迹、扫描记录、签收记录     |
| logistics-route-service    | 网点、运输线路、路线规划         |
| logistics-dispatch-service | 派件任务、司机任务、网点负载     |
| logistics-ai-agent-service | 自然语言问答、异常诊断、调度建议 |
| logistics-common           | 统一返回、异常处理、工具类       |
| logistics-api              | Feign Client、DTO、VO            |

---

# 6. 核心业务流程

## 6.1 用户寄件流程

<pre class="overflow-visible! px-0!" data-start="4977" data-end="5203"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>用户填写寄件信息</span><br/><span>      |</span><br/><span>      v</span><br/><span>创建订单 order-service</span><br/><span>      |</span><br/><span>      v</span><br/><span>生成运单 waybill-service</span><br/><span>      |</span><br/><span>      v</span><br/><span>分配起始网点 route-service</span><br/><span>      |</span><br/><span>      v</span><br/><span>生成揽收任务 dispatch-service</span><br/><span>      |</span><br/><span>      v</span><br/><span>快递员揽收</span><br/><span>      |</span><br/><span>      v</span><br/><span>轨迹更新 track-service</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

## 6.2 运单流转流程

<pre class="overflow-visible! px-0!" data-start="5220" data-end="5286"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>已下单</span><br/><span>  ↓</span><br/><span>待揽收</span><br/><span>  ↓</span><br/><span>已揽收</span><br/><span>  ↓</span><br/><span>运输中</span><br/><span>  ↓</span><br/><span>到达分拨中心</span><br/><span>  ↓</span><br/><span>派送中</span><br/><span>  ↓</span><br/><span>已签收</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

异常状态：

<pre class="overflow-visible! px-0!" data-start="5295" data-end="5341"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>超时未揽收</span><br/><span>运输超时</span><br/><span>网点滞留</span><br/><span>用户拒收</span><br/><span>地址异常</span><br/><span>疑似丢件</span><br/><span>已取消</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

## 6.3 异常件诊断流程

<pre class="overflow-visible! px-0!" data-start="5359" data-end="5476"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>输入运单号</span><br/><span>  ↓</span><br/><span>查询运单基础信息</span><br/><span>  ↓</span><br/><span>查询最近轨迹</span><br/><span>  ↓</span><br/><span>判断当前节点是否超时</span><br/><span>  ↓</span><br/><span>查询所在网点负载</span><br/><span>  ↓</span><br/><span>查询路线是否异常</span><br/><span>  ↓</span><br/><span>查询派件任务状态</span><br/><span>  ↓</span><br/><span>生成异常原因</span><br/><span>  ↓</span><br/><span>给出处理建议</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

# 7. AI Agent 模块设计

## 7.1 Agent 服务定位

`logistics-ai-agent-service` 是整个项目的核心亮点。

它不直接保存核心业务数据，而是通过 Tool Calling 调用其他微服务。

<pre class="overflow-visible! px-0!" data-start="5605" data-end="5673"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>AI Agent 不直接查库</span><br/><span>AI Agent 通过 Tool 调用业务服务</span><br/><span>业务服务负责权限、数据、事务和安全</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

这样设计更安全，也更符合真实企业项目。

Spring AI 文档也强调，模型不会直接访问工具 API，而是由应用程序负责执行工具调用并返回结果，这是做后端 Agent 时必须理解的安全边界。

## 7.2 Agent 角色拆分

<pre class="overflow-visible! px-0!" data-start="5829" data-end="6025"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>SupervisorAgent</span><br/><span>├── WaybillQueryAgent</span><br/><span>├── TrackAnalysisAgent</span><br/><span>├── RoutePlanningAgent</span><br/><span>├── ExceptionDiagnosisAgent</span><br/><span>├── DispatchSuggestAgent</span><br/><span>├── LogisticsReportAgent</span><br/><span>└── KnowledgeBaseAgent</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>


| Agent                   | 职责                             |
| ----------------------- | -------------------------------- |
| SupervisorAgent         | 识别用户意图，决定调用哪个 Agent |
| WaybillQueryAgent       | 查询运单、订单、用户信息         |
| TrackAnalysisAgent      | 分析轨迹是否正常                 |
| RoutePlanningAgent      | 计算运输路线                     |
| ExceptionDiagnosisAgent | 判断异常件原因                   |
| DispatchSuggestAgent    | 给出调度建议                     |
| LogisticsReportAgent    | 生成日报、周报、异常报告         |
| KnowledgeBaseAgent      | 基于物流规则文档进行问答         |

Spring AI Alibaba Agent Framework 支持 SequentialAgent、ParallelAgent、RoutingAgent、LoopAgent 等内置工作流模式，Graph 则适合构建更灵活的多 Agent 工作流。

## 7.3 Agent 调用流程

以异常诊断为例：

<pre class="overflow-visible! px-0!" data-start="6542" data-end="6735"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>用户问题：</span><br/><span>“运单 WB10001 为什么还没签收？”</span><br/><br/><span>SupervisorAgent</span><br/><span>  ↓</span><br/><span>识别意图：异常件诊断</span><br/><span>  ↓</span><br/><span>ExceptionDiagnosisAgent</span><br/><span>  ↓</span><br/><span>调用 WaybillTool</span><br/><span>  ↓</span><br/><span>调用 TrackTool</span><br/><span>  ↓</span><br/><span>调用 RouteTool</span><br/><span>  ↓</span><br/><span>调用 DispatchTool</span><br/><span>  ↓</span><br/><span>汇总结果</span><br/><span>  ↓</span><br/><span>生成解释和建议</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

## 7.4 Tool 设计

<pre class="overflow-visible! px-0!" data-start="6753" data-end="7044"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>@</span><span class="ͼm">Component</span><br/><span class="ͼg">public</span><span> </span><span class="ͼg">class</span><span> </span><span class="ͼm">WaybillTool</span><span> {</span><br/><br/><span>    @</span><span class="ͼm">Resource</span><br/><span>    </span><span class="ͼg">private</span><span> </span><span class="ͼm">WaybillFeignClient</span><span> </span><span class="ͼm">waybillFeignClient</span><span>;</span><br/><br/><span>    @</span><span class="ͼm">Tool</span><span>(</span><span class="ͼm">description</span><span> </span><span class="ͼg">=</span><span> </span><span class="ͼk">"根据运单号查询运单详情"</span><span>)</span><br/><span>    </span><span class="ͼg">public</span><span> </span><span class="ͼm">WaybillDTO</span><span> </span><span class="ͼm">getWaybillByNo</span><span>(</span><span class="ͼm">String</span><span> </span><span class="ͼm">waybillNo</span><span>) {</span><br/><span>        </span><span class="ͼg">return</span><span> </span><span class="ͼm">waybillFeignClient</span><span class="ͼg">.</span><span class="ͼm">getByWaybillNo</span><span>(</span><span class="ͼm">waybillNo</span><span>)</span><span class="ͼg">.</span><span class="ͼm">getData</span><span>();</span><br/><span>    }</span><br/><span>}</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

建议封装以下工具：


| Tool          | 调用服务         | 功能                   |
| ------------- | ---------------- | ---------------------- |
| WaybillTool   | waybill-service  | 查询运单详情           |
| TrackTool     | track-service    | 查询物流轨迹           |
| RouteTool     | route-service    | 查询路线、计算路线     |
| DispatchTool  | dispatch-service | 查询派件任务、司机任务 |
| UserTool      | user-service     | 查询用户、司机、快递员 |
| OrderTool     | order-service    | 查询订单信息           |
| KnowledgeTool | RAG 模块         | 查询物流规则知识库     |
| ReportTool    | ai-agent-service | 生成 Markdown 报告     |

---

# 8. 数据库设计

## 8.1 用户表：t\_user

<pre class="overflow-visible! px-0!" data-start="7474" data-end="7796"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span class="ͼg">CREATE</span><span> </span><span class="ͼg">TABLE</span><span> t_user (</span><br/><span>    id BIGINT </span><span class="ͼg">PRIMARY</span><span> </span><span class="ͼg">KEY</span><span> AUTO_INCREMENT,</span><br/><span>    username </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">64</span><span>) </span><span class="ͼg">NOT</span><span> </span><span class="ͼj">NULL</span><span>,</span><br/><span>    password </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">128</span><span>) </span><span class="ͼg">NOT</span><span> </span><span class="ͼj">NULL</span><span>,</span><br/><span>    phone </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">20</span><span>),</span><br/><span>    </span><span class="ͼg">role</span><span> </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">32</span><span>) </span><span class="ͼg">NOT</span><span> </span><span class="ͼj">NULL</span><span> COMMENT </span><span class="ͼk">'USER, COURIER, DRIVER, ADMIN'</span><span>,</span><br/><span>    status TINYINT </span><span class="ͼg">DEFAULT</span><span> </span><span class="ͼj">1</span><span>,</span><br/><span>    create_time DATETIME,</span><br/><span>    update_time DATETIME</span><br/><span>);</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

## 8.2 订单表：t\_order

<pre class="overflow-visible! px-0!" data-start="7818" data-end="8197"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span class="ͼg">CREATE</span><span> </span><span class="ͼg">TABLE</span><span> t_order (</span><br/><span>    id BIGINT </span><span class="ͼg">PRIMARY</span><span> </span><span class="ͼg">KEY</span><span> AUTO_INCREMENT,</span><br/><span>    order_no </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">64</span><span>) </span><span class="ͼg">NOT</span><span> </span><span class="ͼj">NULL</span><span> </span><span class="ͼg">UNIQUE</span><span>,</span><br/><span>    sender_id BIGINT </span><span class="ͼg">NOT</span><span> </span><span class="ͼj">NULL</span><span>,</span><br/><span>    receiver_name </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">64</span><span>),</span><br/><span>    receiver_phone </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">20</span><span>),</span><br/><span>    receiver_address </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">255</span><span>),</span><br/><span>    goods_name </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">128</span><span>),</span><br/><span>    weight </span><span class="ͼm">DECIMAL</span><span>(</span><span class="ͼj">10</span><span>,</span><span class="ͼj">2</span><span>),</span><br/><span>    status </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">32</span><span>),</span><br/><span>    create_time DATETIME,</span><br/><span>    update_time DATETIME</span><br/><span>);</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

## 8.3 运单表：t\_waybill

<pre class="overflow-visible! px-0!" data-start="8221" data-end="8591"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span class="ͼg">CREATE</span><span> </span><span class="ͼg">TABLE</span><span> t_waybill (</span><br/><span>    id BIGINT </span><span class="ͼg">PRIMARY</span><span> </span><span class="ͼg">KEY</span><span> AUTO_INCREMENT,</span><br/><span>    waybill_no </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">64</span><span>) </span><span class="ͼg">NOT</span><span> </span><span class="ͼj">NULL</span><span> </span><span class="ͼg">UNIQUE</span><span>,</span><br/><span>    order_no </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">64</span><span>) </span><span class="ͼg">NOT</span><span> </span><span class="ͼj">NULL</span><span>,</span><br/><span>    current_node_id BIGINT,</span><br/><span>    current_status </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">32</span><span>),</span><br/><span>    exception_type </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">64</span><span>),</span><br/><span>    expected_arrive_time DATETIME,</span><br/><span>    actual_arrive_time DATETIME,</span><br/><span>    create_time DATETIME,</span><br/><span>    update_time DATETIME</span><br/><span>);</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

## 8.4 轨迹表：t\_track

<pre class="overflow-visible! px-0!" data-start="8613" data-end="8935"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span class="ͼg">CREATE</span><span> </span><span class="ͼg">TABLE</span><span> t_track (</span><br/><span>    id BIGINT </span><span class="ͼg">PRIMARY</span><span> </span><span class="ͼg">KEY</span><span> AUTO_INCREMENT,</span><br/><span>    waybill_no </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">64</span><span>) </span><span class="ͼg">NOT</span><span> </span><span class="ͼj">NULL</span><span>,</span><br/><span>    node_id BIGINT,</span><br/><span>    </span><span class="ͼg">action</span><span> </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">64</span><span>) COMMENT </span><span class="ͼk">'COLLECTED, ARRIVED, DEPARTED, DELIVERING, SIGNED'</span><span>,</span><br/><span>    description </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">255</span><span>),</span><br/><span>    operator_id BIGINT,</span><br/><span>    event_time DATETIME,</span><br/><span>    create_time DATETIME</span><br/><span>);</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

## 8.5 网点表：t\_station

<pre class="overflow-visible! px-0!" data-start="8959" data-end="9291"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span class="ͼg">CREATE</span><span> </span><span class="ͼg">TABLE</span><span> t_station (</span><br/><span>    id BIGINT </span><span class="ͼg">PRIMARY</span><span> </span><span class="ͼg">KEY</span><span> AUTO_INCREMENT,</span><br/><span>    station_name </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">128</span><span>) </span><span class="ͼg">NOT</span><span> </span><span class="ͼj">NULL</span><span>,</span><br/><span>    province </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">64</span><span>),</span><br/><span>    city </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">64</span><span>),</span><br/><span>    address </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">255</span><span>),</span><br/><span>    longitude </span><span class="ͼm">DECIMAL</span><span>(</span><span class="ͼj">10</span><span>,</span><span class="ͼj">6</span><span>),</span><br/><span>    latitude </span><span class="ͼm">DECIMAL</span><span>(</span><span class="ͼj">10</span><span>,</span><span class="ͼj">6</span><span>),</span><br/><span>    status TINYINT </span><span class="ͼg">DEFAULT</span><span> </span><span class="ͼj">1</span><span>,</span><br/><span>    create_time DATETIME,</span><br/><span>    update_time DATETIME</span><br/><span>);</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

## 8.6 路线表：t\_route

<pre class="overflow-visible! px-0!" data-start="9313" data-end="9623"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span class="ͼg">CREATE</span><span> </span><span class="ͼg">TABLE</span><span> t_route (</span><br/><span>    id BIGINT </span><span class="ͼg">PRIMARY</span><span> </span><span class="ͼg">KEY</span><span> AUTO_INCREMENT,</span><br/><span>    start_station_id BIGINT </span><span class="ͼg">NOT</span><span> </span><span class="ͼj">NULL</span><span>,</span><br/><span>    end_station_id BIGINT </span><span class="ͼg">NOT</span><span> </span><span class="ͼj">NULL</span><span>,</span><br/><span>    distance </span><span class="ͼm">DECIMAL</span><span>(</span><span class="ͼj">10</span><span>,</span><span class="ͼj">2</span><span>),</span><br/><span>    estimated_hours </span><span class="ͼm">INT</span><span>,</span><br/><span>    cost </span><span class="ͼm">DECIMAL</span><span>(</span><span class="ͼj">10</span><span>,</span><span class="ͼj">2</span><span>),</span><br/><span>    status TINYINT </span><span class="ͼg">DEFAULT</span><span> </span><span class="ͼj">1</span><span>,</span><br/><span>    create_time DATETIME,</span><br/><span>    update_time DATETIME</span><br/><span>);</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

## 8.7 派件任务表：t\_dispatch\_task

<pre class="overflow-visible! px-0!" data-start="9655" data-end="9997"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span class="ͼg">CREATE</span><span> </span><span class="ͼg">TABLE</span><span> t_dispatch_task (</span><br/><span>    id BIGINT </span><span class="ͼg">PRIMARY</span><span> </span><span class="ͼg">KEY</span><span> AUTO_INCREMENT,</span><br/><span>    task_no </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">64</span><span>) </span><span class="ͼg">NOT</span><span> </span><span class="ͼj">NULL</span><span> </span><span class="ͼg">UNIQUE</span><span>,</span><br/><span>    waybill_no </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">64</span><span>) </span><span class="ͼg">NOT</span><span> </span><span class="ͼj">NULL</span><span>,</span><br/><span>    courier_id BIGINT,</span><br/><span>    station_id BIGINT,</span><br/><span>    task_status </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">32</span><span>),</span><br/><span>    assign_time DATETIME,</span><br/><span>    finish_time DATETIME,</span><br/><span>    create_time DATETIME,</span><br/><span>    update_time DATETIME</span><br/><span>);</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

## 8.8 Agent 会话表：t\_agent\_session

<pre class="overflow-visible! px-0!" data-start="10033" data-end="10268"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span class="ͼg">CREATE</span><span> </span><span class="ͼg">TABLE</span><span> t_agent_session (</span><br/><span>    id BIGINT </span><span class="ͼg">PRIMARY</span><span> </span><span class="ͼg">KEY</span><span> AUTO_INCREMENT,</span><br/><span>    session_id </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">64</span><span>) </span><span class="ͼg">NOT</span><span> </span><span class="ͼj">NULL</span><span> </span><span class="ͼg">UNIQUE</span><span>,</span><br/><span>    user_id BIGINT,</span><br/><span>    question TEXT,</span><br/><span>    intent </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">64</span><span>),</span><br/><span>    answer TEXT,</span><br/><span>    create_time DATETIME</span><br/><span>);</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

## 8.9 Agent 工具调用日志表：t\_agent\_tool\_log

<pre class="overflow-visible! px-0!" data-start="10309" data-end="10589"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span class="ͼg">CREATE</span><span> </span><span class="ͼg">TABLE</span><span> t_agent_tool_log (</span><br/><span>    id BIGINT </span><span class="ͼg">PRIMARY</span><span> </span><span class="ͼg">KEY</span><span> AUTO_INCREMENT,</span><br/><span>    session_id </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">64</span><span>),</span><br/><span>    tool_name </span><span class="ͼm">VARCHAR</span><span>(</span><span class="ͼj">128</span><span>),</span><br/><span>    request_params TEXT,</span><br/><span>    response_result TEXT,</span><br/><span>    success TINYINT,</span><br/><span>    error_msg TEXT,</span><br/><span>    cost_ms BIGINT,</span><br/><span>    create_time DATETIME</span><br/><span>);</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

# 9. 接口设计

## 9.1 统一返回格式

<pre class="overflow-visible! px-0!" data-start="10622" data-end="10998"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>@</span><span class="ͼm">Data</span><br/><span>@</span><span class="ͼm">AllArgsConstructor</span><br/><span>@</span><span class="ͼm">NoArgsConstructor</span><br/><span class="ͼg">public</span><span> </span><span class="ͼg">class</span><span> </span><span class="ͼm">Result</span><span><</span><span class="ͼm">T</span><span>> {</span><br/><span>    </span><span class="ͼg">private</span><span> </span><span class="ͼm">Integer</span><span> </span><span class="ͼm">code</span><span>;</span><br/><span>    </span><span class="ͼg">private</span><span> </span><span class="ͼm">String</span><span> </span><span class="ͼm">message</span><span>;</span><br/><span>    </span><span class="ͼg">private</span><span> </span><span class="ͼm">T</span><span> </span><span class="ͼm">data</span><span>;</span><br/><br/><span>    </span><span class="ͼg">public</span><span> </span><span class="ͼg">static</span><span> <</span><span class="ͼm">T</span><span>> </span><span class="ͼm">Result</span><span><</span><span class="ͼm">T</span><span>> </span><span class="ͼm">success</span><span>(</span><span class="ͼm">T</span><span> </span><span class="ͼm">data</span><span>) {</span><br/><span>        </span><span class="ͼg">return</span><span> </span><span class="ͼg">new</span><span> </span><span class="ͼm">Result</span><span><>(</span><span class="ͼj">200</span><span>, </span><span class="ͼk">"success"</span><span>, </span><span class="ͼm">data</span><span>);</span><br/><span>    }</span><br/><br/><span>    </span><span class="ͼg">public</span><span> </span><span class="ͼg">static</span><span> <</span><span class="ͼm">T</span><span>> </span><span class="ͼm">Result</span><span><</span><span class="ͼm">T</span><span>> </span><span class="ͼm">fail</span><span>(</span><span class="ͼm">String</span><span> </span><span class="ͼm">message</span><span>) {</span><br/><span>        </span><span class="ͼg">return</span><span> </span><span class="ͼg">new</span><span> </span><span class="ͼm">Result</span><span><>(</span><span class="ͼj">500</span><span>, </span><span class="ͼm">message</span><span>, </span><span class="ͼj">null</span><span>);</span><br/><span>    }</span><br/><span>}</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

## 9.2 订单服务接口

### 创建订单

<pre class="overflow-visible! px-0!" data-start="11030" data-end="11058"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>POST /api/orders</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

请求参数：

<pre class="overflow-visible! px-0!" data-start="11067" data-end="11233"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>{</span><br/><span>  "senderId": </span><span class="ͼj">1</span><span>,</span><br/><span>  "receiverName": </span><span class="ͼk">"张三"</span><span>,</span><br/><span>  "receiverPhone": </span><span class="ͼk">"13800000000"</span><span>,</span><br/><span>  "receiverAddress": </span><span class="ͼk">"广东省深圳市南山区科技园"</span><span>,</span><br/><span>  "goodsName": </span><span class="ͼk">"电子产品"</span><span>,</span><br/><span>  "weight": </span><span class="ͼj">2.5</span><br/><span>}</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

返回：

<pre class="overflow-visible! px-0!" data-start="11240" data-end="11342"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>{</span><br/><span>  "code": </span><span class="ͼj">200</span><span>,</span><br/><span>  "message": </span><span class="ͼk">"success"</span><span>,</span><br/><span>  "data": {</span><br/><span>    "orderNo": </span><span class="ͼk">"OD202605090001"</span><br/><span>  }</span><br/><span>}</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

### 查询订单详情

<pre class="overflow-visible! px-0!" data-start="11356" data-end="11393"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>GET /api/orders/{orderNo}</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

## 9.3 运单服务接口

### 根据运单号查询运单

<pre class="overflow-visible! px-0!" data-start="11430" data-end="11471"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>GET /api/waybills/{waybillNo}</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

返回：

<pre class="overflow-visible! px-0!" data-start="11478" data-end="11679"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>{</span><br/><span>  "waybillNo": </span><span class="ͼk">"WB202605090001"</span><span>,</span><br/><span>  "orderNo": </span><span class="ͼk">"OD202605090001"</span><span>,</span><br/><span>  "currentStatus": </span><span class="ͼk">"运输中"</span><span>,</span><br/><span>  "currentNodeId": </span><span class="ͼj">1001</span><span>,</span><br/><span>  "exceptionType": </span><span class="ͼj">null</span><span>,</span><br/><span>  "expectedArriveTime": </span><span class="ͼk">"2026-05-10 18:00:00"</span><br/><span>}</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

### 标记异常件

<pre class="overflow-visible! px-0!" data-start="11692" data-end="11744"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>POST /api/waybills/{waybillNo}/exception</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

请求参数：

<pre class="overflow-visible! px-0!" data-start="11753" data-end="11835"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>{</span><br/><span>  "exceptionType": </span><span class="ͼk">"TRANSPORT_TIMEOUT"</span><span>,</span><br/><span>  "reason": </span><span class="ͼk">"运输节点超过预计到达时间"</span><br/><span>}</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

## 9.4 轨迹服务接口

### 查询运单轨迹

<pre class="overflow-visible! px-0!" data-start="11869" data-end="11916"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>GET /api/tracks/waybill/{waybillNo}</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

返回：

<pre class="overflow-visible! px-0!" data-start="11923" data-end="12148"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>[</span><br/><span>  {</span><br/><span>    "action": </span><span class="ͼk">"COLLECTED"</span><span>,</span><br/><span>    "description": </span><span class="ͼk">"快递员已揽收"</span><span>,</span><br/><span>    "eventTime": </span><span class="ͼk">"2026-05-09 10:00:00"</span><br/><span>  },</span><br/><span>  {</span><br/><span>    "action": </span><span class="ͼk">"ARRIVED"</span><span>,</span><br/><span>    "description": </span><span class="ͼk">"快件已到达广州分拨中心"</span><span>,</span><br/><span>    "eventTime": </span><span class="ͼk">"2026-05-09 15:00:00"</span><br/><span>  }</span><br/><span>]</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

### 添加轨迹

<pre class="overflow-visible! px-0!" data-start="12160" data-end="12188"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>POST /api/tracks</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

## 9.5 路线服务接口

### 查询可用路线

<pre class="overflow-visible! px-0!" data-start="12222" data-end="12281"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>GET /api/routes?startStationId=1&endStationId=5</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

### 计算最优路线

<pre class="overflow-visible! px-0!" data-start="12295" data-end="12328"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>POST /api/routes/plan</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

请求参数：

<pre class="overflow-visible! px-0!" data-start="12337" data-end="12423"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>{</span><br/><span>  "startStationId": </span><span class="ͼj">1</span><span>,</span><br/><span>  "endStationId": </span><span class="ͼj">5</span><span>,</span><br/><span>  "strategy": </span><span class="ͼk">"TIME_FIRST"</span><br/><span>}</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

返回：

<pre class="overflow-visible! px-0!" data-start="12430" data-end="12545"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>{</span><br/><span>  "path": [</span><span class="ͼj">1</span><span>, </span><span class="ͼj">2</span><span>, </span><span class="ͼj">4</span><span>, </span><span class="ͼj">5</span><span>],</span><br/><span>  "totalDistance": </span><span class="ͼj">138.5</span><span>,</span><br/><span>  "estimatedHours": </span><span class="ͼj">6</span><span>,</span><br/><span>  "strategy": </span><span class="ͼk">"TIME_FIRST"</span><br/><span>}</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

## 9.6 调度服务接口

### 查询网点负载

<pre class="overflow-visible! px-0!" data-start="12579" data-end="12634"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>GET /api/dispatch/stations/{stationId}/load</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

返回：

<pre class="overflow-visible! px-0!" data-start="12641" data-end="12771"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>{</span><br/><span>  "stationId": </span><span class="ͼj">1001</span><span>,</span><br/><span>  "pendingWaybillCount": </span><span class="ͼj">320</span><span>,</span><br/><span>  "courierCount": </span><span class="ͼj">12</span><span>,</span><br/><span>  "driverCount": </span><span class="ͼj">5</span><span>,</span><br/><span>  "loadLevel": </span><span class="ͼk">"HIGH"</span><br/><span>}</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

### 创建派件任务

<pre class="overflow-visible! px-0!" data-start="12785" data-end="12821"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>POST /api/dispatch/tasks</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

## 9.7 AI Agent 服务接口

### 智能问答

<pre class="overflow-visible! px-0!" data-start="12860" data-end="12892"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>POST /api/agent/chat</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

请求参数：

<pre class="overflow-visible! px-0!" data-start="12901" data-end="12976"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>{</span><br/><span>  "userId": </span><span class="ͼj">1</span><span>,</span><br/><span>  "message": </span><span class="ͼk">"帮我分析运单 WB202605090001 为什么还没签收"</span><br/><span>}</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

返回：

<pre class="overflow-visible! px-0!" data-start="12983" data-end="13311"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>{</span><br/><span>  "sessionId": </span><span class="ͼk">"AGENT202605090001"</span><span>,</span><br/><span>  "intent": </span><span class="ͼk">"WAYBILL_EXCEPTION_DIAGNOSIS"</span><span>,</span><br/><span>  "answer": </span><span class="ͼk">"该运单当前停留在广州分拨中心，最近一次轨迹更新时间为 2026-05-09 15:00，距离当前已超过预计中转时间。结合网点负载数据，该网点当前负载较高，建议优先安排下一班运输车辆。"</span><span>,</span><br/><span>  "toolCalls": [</span><br/><span>    </span><span class="ͼk">"WaybillTool.getWaybillByNo"</span><span>,</span><br/><span>    </span><span class="ͼk">"TrackTool.getTracksByWaybillNo"</span><span>,</span><br/><span>    </span><span class="ͼk">"DispatchTool.getStationLoad"</span><br/><span>  ]</span><br/><span>}</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

### 异常件诊断

<pre class="overflow-visible! px-0!" data-start="13324" data-end="13368"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>POST /api/agent/diagnose/waybill</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

请求参数：

<pre class="overflow-visible! px-0!" data-start="13377" data-end="13424"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>{</span><br/><span>  "waybillNo": </span><span class="ͼk">"WB202605090001"</span><br/><span>}</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

### 生成物流日报

<pre class="overflow-visible! px-0!" data-start="13438" data-end="13478"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>POST /api/agent/report/daily</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

请求参数：

<pre class="overflow-visible! px-0!" data-start="13487" data-end="13546"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>{</span><br/><span>  "date": </span><span class="ͼk">"2026-05-09"</span><span>,</span><br/><span>  "stationId": </span><span class="ͼj">1001</span><br/><span>}</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

# 10. Agent 意图识别设计

## 10.1 意图枚举

<pre class="overflow-visible! px-0!" data-start="13587" data-end="13771"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span class="ͼg">public</span><span> </span><span class="ͼg">enum</span><span> </span><span class="ͼm">AgentIntent</span><span> {</span><br/><span>    </span><span class="ͼm">WAYBILL_QUERY</span><span>,</span><br/><span>    </span><span class="ͼm">WAYBILL_EXCEPTION_DIAGNOSIS</span><span>,</span><br/><span>    </span><span class="ͼm">ROUTE_PLANNING</span><span>,</span><br/><span>    </span><span class="ͼm">DISPATCH_SUGGESTION</span><span>,</span><br/><span>    </span><span class="ͼm">DAILY_REPORT</span><span>,</span><br/><span>    </span><span class="ͼm">KNOWLEDGE_QA</span><span>,</span><br/><span>    </span><span class="ͼm">UNKNOWN</span><br/><span>}</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

## 10.2 意图示例


| 用户问题                | 识别意图                      |
| ----------------------- | ----------------------------- |
| 查询一下 WB10001 到哪了 | WAYBILL\_QUERY                |
| 这个运单为什么还没签收  | WAYBILL\_EXCEPTION\_DIAGNOSIS |
| 广州到深圳怎么走最快    | ROUTE\_PLANNING               |
| 今天哪个网点压力最大    | DISPATCH\_SUGGESTION          |
| 生成今天物流日报        | DAILY\_REPORT                 |
| 拒收件应该怎么处理      | KNOWLEDGE\_QA                 |

---

# 11. 路线规划算法设计

你有算法竞赛经历，这里一定要做成亮点。

## 11.1 路线建模

将物流网点看成图：

<pre class="overflow-visible! px-0!" data-start="14090" data-end="14138"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>站点 = 图的节点</span><br/><span>线路 = 图的边</span><br/><span>距离 / 时间 / 成本 = 边权</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

## 11.2 支持策略


| 策略       | 算法                       |
| ---------- | -------------------------- |
| 距离最短   | Dijkstra                   |
| 时间最短   | Dijkstra                   |
| 成本最低   | Dijkstra                   |
| 综合最优   | 加权评分                   |
| 多目标路线 | 距离 + 时间 + 成本综合排序 |

## 11.3 综合评分公式

<pre class="overflow-visible! px-0!" data-start="14298" data-end="14367"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>score = distance * 0.4 + estimatedTime * 0.4 + cost * 0.2</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

后期可升级为：

<pre class="overflow-visible! px-0!" data-start="14378" data-end="14517"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>score = distanceWeight * distance</span><br/><span>      + timeWeight * estimatedTime</span><br/><span>      + costWeight * cost</span><br/><span>      + loadWeight * stationLoad</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

# 12. RAG 知识库设计

## 12.1 知识库内容

你可以准备一些 Markdown 文档：

<pre class="overflow-visible! px-0!" data-start="14578" data-end="14692"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>docs/knowledge</span><br/><span>├── 异常件处理规则.md</span><br/><span>├── 派件超时处理规范.md</span><br/><span>├── 拒收件处理流程.md</span><br/><span>├── 丢件赔付规则.md</span><br/><span>├── 网点调度规范.md</span><br/><span>└── 客服话术模板.md</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

## 12.2 RAG 使用场景


| 场景     | 示例                           |
| -------- | ------------------------------ |
| 客服问答 | “客户拒收了怎么办？”         |
| 异常处理 | “运输超时应该如何处理？”     |
| 调度规范 | “网点爆仓时优先处理哪些件？” |
| 新人学习 | “物流系统有哪些状态？”       |

## 12.3 RAG 流程

<pre class="overflow-visible! px-0!" data-start="14850" data-end="14916"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>用户问题</span><br/><span>  ↓</span><br/><span>向量化</span><br/><span>  ↓</span><br/><span>检索相关物流文档</span><br/><span>  ↓</span><br/><span>拼接上下文</span><br/><span>  ↓</span><br/><span>调用大模型</span><br/><span>  ↓</span><br/><span>生成答案</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

# 13. 项目目录结构

<pre class="overflow-visible! px-0!" data-start="14937" data-end="15990"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>logiagent</span><br/><span>├── pom.xml</span><br/><span>├── docker-compose.yml</span><br/><span>├── README.md</span><br/><span>├── docs</span><br/><span>│   ├── 开发文档.md</span><br/><span>│   ├── 接口文档.md</span><br/><span>│   ├── 数据库设计.md</span><br/><span>│   ├── 部署文档.md</span><br/><span>│   └── knowledge</span><br/><span>│       ├── 异常件处理规则.md</span><br/><span>│       └── 派件超时处理规范.md</span><br/><span>├── logistics-common</span><br/><span>│   ├── exception</span><br/><span>│   ├── result</span><br/><span>│   ├── utils</span><br/><span>│   └── constants</span><br/><span>├── logistics-api</span><br/><span>│   ├── user-api</span><br/><span>│   ├── order-api</span><br/><span>│   ├── waybill-api</span><br/><span>│   ├── track-api</span><br/><span>│   ├── route-api</span><br/><span>│   └── dispatch-api</span><br/><span>├── logistics-gateway</span><br/><span>├── logistics-auth-service</span><br/><span>├── logistics-user-service</span><br/><span>├── logistics-order-service</span><br/><span>├── logistics-waybill-service</span><br/><span>├── logistics-track-service</span><br/><span>├── logistics-route-service</span><br/><span>├── logistics-dispatch-service</span><br/><span>└── logistics-ai-agent-service</span><br/><span>    ├── agent</span><br/><span>    │   ├── SupervisorAgent.java</span><br/><span>    │   ├── WaybillQueryAgent.java</span><br/><span>    │   ├── ExceptionDiagnosisAgent.java</span><br/><span>    │   ├── RoutePlanningAgent.java</span><br/><span>    │   └── ReportAgent.java</span><br/><span>    ├── tool</span><br/><span>    │   ├── WaybillTool.java</span><br/><span>    │   ├── TrackTool.java</span><br/><span>    │   ├── RouteTool.java</span><br/><span>    │   └── DispatchTool.java</span><br/><span>    ├── rag</span><br/><span>    ├── controller</span><br/><span>    ├── service</span><br/><span>    └── config</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

# 14. 开发阶段规划

## 第一阶段：基础微服务搭建

目标：跑通基础架构。

任务：

<pre class="overflow-visible! px-0!" data-start="16045" data-end="16188"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>1. 创建 Maven 父工程</span><br/><span>2. 创建 common、api、gateway、auth、user 服务</span><br/><span>3. 接入 Nacos 注册中心</span><br/><span>4. 配置 Gateway 路由</span><br/><span>5. 实现统一返回 Result</span><br/><span>6. 实现统一异常处理</span><br/><span>7. 实现 JWT 登录认证</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

产出：

<pre class="overflow-visible! px-0!" data-start="16195" data-end="16236"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>用户可以登录</span><br/><span>服务可以注册到 Nacos</span><br/><span>网关可以转发请求</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

## 第二阶段：物流核心业务

目标：完成最小物流闭环。

任务：

<pre class="overflow-visible! px-0!" data-start="16278" data-end="16387"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>1. 实现订单服务</span><br/><span>2. 实现运单服务</span><br/><span>3. 实现轨迹服务</span><br/><span>4. 实现路线服务</span><br/><span>5. 实现调度服务</span><br/><span>6. 使用 OpenFeign 完成服务调用</span><br/><span>7. 使用 RabbitMQ 异步创建运单和轨迹</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

产出：

<pre class="overflow-visible! px-0!" data-start="16394" data-end="16431"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>用户下单 → 生成运单 → 更新轨迹 → 查询状态</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

## 第三阶段：路线规划与异常件

目标：做出项目差异化。

任务：

<pre class="overflow-visible! px-0!" data-start="16474" data-end="16555"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>1. 建立网点和路线数据</span><br/><span>2. 实现 Dijkstra 路线规划</span><br/><span>3. 实现异常件判断规则</span><br/><span>4. 实现网点负载统计</span><br/><span>5. 实现派件任务分配</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

产出：

<pre class="overflow-visible! px-0!" data-start="16562" data-end="16602"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>可以计算路线</span><br/><span>可以判断运输超时、网点滞留、拒收、疑似丢件</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

## 第四阶段：AI Agent 服务

目标：实现智能助手能力。

任务：

<pre class="overflow-visible! px-0!" data-start="16649" data-end="16857"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>1. 创建 logistics-ai-agent-service</span><br/><span>2. 接入 Spring AI / Spring AI Alibaba</span><br/><span>3. 封装 WaybillTool、TrackTool、RouteTool、DispatchTool</span><br/><span>4. 实现 SupervisorAgent</span><br/><span>5. 实现异常件诊断 Agent</span><br/><span>6. 实现物流日报 Agent</span><br/><span>7. 保存 Agent 会话和工具调用日志</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

产出：

<pre class="overflow-visible! px-0!" data-start="16864" data-end="16899"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>用户可以用自然语言查询运单、分析异常、生成日报</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

## 第五阶段：RAG 知识库

目标：增强项目高级感。

任务：

<pre class="overflow-visible! px-0!" data-start="16941" data-end="17014"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>1. 编写物流规则 Markdown 文档</span><br/><span>2. 文档切分</span><br/><span>3. 向量化入库</span><br/><span>4. 实现知识库问答</span><br/><span>5. 和异常件诊断结合</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

产出：

<pre class="overflow-visible! px-0!" data-start="17021" data-end="17055"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>用户可以问物流规则、异常件处理流程、客服话术</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

## 第六阶段：前端和部署

目标：做成可演示项目。

任务：

<pre class="overflow-visible! px-0!" data-start="17095" data-end="17225"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>1. Vue3 + Element Plus 搭建后台</span><br/><span>2. 实现订单、运单、轨迹、网点页面</span><br/><span>3. 实现 Agent 聊天页面</span><br/><span>4. 实现异常件分析报告页面</span><br/><span>5. 编写 Docker Compose</span><br/><span>6. 编写 README 和接口文档</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

产出：

<pre class="overflow-visible! px-0!" data-start="17232" data-end="17252"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>项目可以完整演示</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

# 15. MVP 最小可用版本

你不要一开始就做全量版本，建议 MVP 只做这些：

<pre class="overflow-visible! px-0!" data-start="17304" data-end="17370"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>1. 网关服务</span><br/><span>2. 认证服务</span><br/><span>3. 订单服务</span><br/><span>4. 运单服务</span><br/><span>5. 轨迹服务</span><br/><span>6. AI Agent 服务</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

MVP 功能：

<pre class="overflow-visible! px-0!" data-start="17381" data-end="17437"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>用户登录</span><br/><span>创建订单</span><br/><span>生成运单</span><br/><span>添加轨迹</span><br/><span>查询运单</span><br/><span>AI 分析运单异常</span><br/><span>AI 生成物流日报</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

这已经足够写进简历。

---

# 16. 可扩展功能

后续可以继续加：

<pre class="overflow-visible! px-0!" data-start="17479" data-end="17664"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>1. Elasticsearch 搜索运单</span><br/><span>2. Redis 缓存热门运单</span><br/><span>3. RabbitMQ 异步轨迹更新</span><br/><span>4. Sentinel 接口限流</span><br/><span>5. Seata 分布式事务</span><br/><span>6. SkyWalking 链路追踪</span><br/><span>7. Docker Compose 一键部署</span><br/><span>8. RAG 物流知识库</span><br/><span>9. 多 Agent 工作流编排</span><br/><span>10. MCP 工具服务</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

# 17. 面试可讲重点

你面试时可以重点讲这几个点：

## 17.1 为什么要拆成微服务？

因为物流系统天然有多个业务域：

<pre class="overflow-visible! px-0!" data-start="17738" data-end="17784"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>用户域</span><br/><span>订单域</span><br/><span>运单域</span><br/><span>轨迹域</span><br/><span>路线域</span><br/><span>调度域</span><br/><span>AI Agent 域</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

拆分后，每个服务职责更清晰，也方便独立扩展。

## 17.2 Agent 为什么不直接查数据库？

因为直接让 Agent 查数据库有安全风险。更好的方式是：

<pre class="overflow-visible! px-0!" data-start="17868" data-end="17920"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>Agent → Tool → Feign Client → 业务服务 → 数据库</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

这样可以复用原有服务的权限校验、参数校验、日志记录和异常处理。

## 17.3 项目里的算法亮点是什么？

路线规划可以抽象成图论问题：

<pre class="overflow-visible! px-0!" data-start="17993" data-end="18029"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>网点是节点</span><br/><span>运输线路是边</span><br/><span>距离、时间、成本是权重</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

使用 Dijkstra 实现最短路径，再扩展成多权重综合评分。

## 17.4 项目里的 AI 亮点是什么？

不是简单聊天，而是：

<pre class="overflow-visible! px-0!" data-start="18100" data-end="18148"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>自然语言理解</span><br/><span>任务拆解</span><br/><span>工具调用</span><br/><span>多服务数据聚合</span><br/><span>异常原因分析</span><br/><span>报告生成</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

# 18. 简历写法

可以这样写：

**LogiAgent｜基于 Spring Cloud Alibaba 的智能物流调度 Agent 系统**

* 基于 Spring Cloud Alibaba 构建物流微服务系统，拆分网关、认证、订单、运单、轨迹、路线、调度、AI Agent 等服务模块。
* 使用 Nacos 实现服务注册与配置管理，使用 Gateway 统一路由请求，使用 OpenFeign 完成服务间远程调用。
* 设计运单、轨迹、网点、路线、派件任务等核心业务模型，实现用户下单、生成运单、物流轨迹更新和异常件标记流程。
* 引入 Spring AI / Spring AI Alibaba 构建智能体服务，设计 SupervisorAgent 对用户意图进行识别与路由。
* 基于 Tool Calling 封装运单查询、轨迹查询、路线规划、网点负载查询等工具，实现自然语言运单查询和异常件诊断。
* 将物流网点与运输线路抽象为图结构，使用 Dijkstra 算法实现最短路径规划，并支持按时间、距离、成本进行路线推荐。
* 构建物流规则 RAG 知识库，支持异常件处理规范、客服话术、派件规则等自然语言问答。
* 实现物流日报生成功能，自动统计订单量、异常件数量、签收率和网点负载，并生成 Markdown 分析报告。

---

# 19. 推荐项目开发顺序

你现在可以按这个顺序做：

<pre class="overflow-visible! px-0!" data-start="18764" data-end="18966"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>第 1 周：搭建父工程、Nacos、Gateway、common、auth</span><br/><span>第 2 周：完成订单服务、运单服务、轨迹服务</span><br/><span>第 3 周：完成路线服务、调度服务、异常件规则</span><br/><span>第 4 周：接入 Spring AI，实现 Agent Tool Calling</span><br/><span>第 5 周：实现异常件诊断 Agent、物流日报 Agent</span><br/><span>第 6 周：做前端页面、Docker 部署、README、简历包装</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

# 20. 最终建议

你这个项目不要追求“大而全”，重点是做出这三个核心闭环：

<pre class="overflow-visible! px-0!" data-start="19015" data-end="19166"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="relative"><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼd ͼr"><div class="cm-scroller"><pre class="cm-content q9tKkq_readonly m-0"><code><span>业务闭环：</span><br/><span>下单 → 运单 → 轨迹 → 签收 / 异常</span><br/><br/><span>微服务闭环：</span><br/><span>Gateway → Feign → Nacos → Redis / MySQL → RabbitMQ</span><br/><br/><span>Agent 闭环：</span><br/><span>用户问题 → 意图识别 → 工具调用 → 多服务查询 → 智能分析 → 报告生成</span></code></pre></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

只要这三个闭环做扎实，**LogiAgent 会比普通商城、若依二开、单体 RAG 项目更适合你找 Java 后端 / AI 应用开发 / Agent 开发实习**。
