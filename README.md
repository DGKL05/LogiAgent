# LogiAgent

LogiAgent is a Java 17 + Spring Cloud Alibaba logistics microservice project with an AI Agent service planned as the main highlight.

This repository is currently at the MVP skeleton stage. The goal of this stage is to provide a clean, runnable multi-module foundation before implementing order, waybill, track, and Agent business flows.

## Tech Stack

- Java 17
- Spring Boot 3.5.14
- Spring Cloud 2025.0.2
- Spring Cloud Alibaba 2025.0.0.0
- Spring Cloud Gateway
- OpenFeign
- Nacos
- Maven multi-module project

## Modules

```text
logiagent
├── logistics-common              Common Result, ErrorCode, BusinessException
├── logistics-api                 Shared Feign/API DTO package placeholders
├── logistics-gateway             Gateway and route placeholders
├── logistics-auth-service        Auth service skeleton
├── logistics-order-service       Order service skeleton
├── logistics-waybill-service     Waybill service skeleton
├── logistics-track-service       Track service skeleton
└── logistics-ai-agent-service    AI Agent service skeleton
```

MVP-only modules are included first. `user`, `route`, and `dispatch` modules are intentionally left for later stages.

## Current Public Interface

Real business APIs are not implemented yet. Each runnable service exposes only:

```http
GET /internal/health
```

Example response:

```json
{
  "code": 200,
  "message": "success",
  "data": "logistics-auth-service"
}
```

## Gateway Routes

The gateway contains route placeholders for the MVP services:

| Path | Target |
| --- | --- |
| `/api/auth/**` | `lb://logistics-auth-service` |
| `/api/orders/**` | `lb://logistics-order-service` |
| `/api/waybills/**` | `lb://logistics-waybill-service` |
| `/api/tracks/**` | `lb://logistics-track-service` |
| `/api/agent/**` | `lb://logistics-ai-agent-service` |

## Local Startup

### 1. Start Nacos

```bash
docker compose up -d nacos
```

Nacos console:

```text
http://localhost:8848/nacos
```

Default username/password is usually `nacos` / `nacos`.

### 2. Build

```bash
mvn clean package -DskipTests
```

Install local module artifacts once before running a single service module:

```bash
mvn install -DskipTests
```

### 3. Run Services

```bash
mvn spring-boot:run -pl logistics-gateway
mvn spring-boot:run -pl logistics-auth-service
mvn spring-boot:run -pl logistics-order-service
mvn spring-boot:run -pl logistics-waybill-service
mvn spring-boot:run -pl logistics-track-service
mvn spring-boot:run -pl logistics-ai-agent-service
```

Service ports:

| Service | Port |
| --- | ---: |
| logistics-gateway | 8080 |
| logistics-auth-service | 9001 |
| logistics-order-service | 9002 |
| logistics-waybill-service | 9003 |
| logistics-track-service | 9004 |
| logistics-ai-agent-service | 9005 |

## Roadmap

1. Implement auth login and JWT issuing.
2. Implement order, waybill, and track MVP flows.
3. Add Feign calls between services.
4. Implement AI Agent tool wrappers through Feign clients.
5. Add exception diagnosis and simple logistics daily report.

## Notes

- Do not commit real API keys, tokens, database passwords, phone numbers, addresses, or ID numbers.
- AI Agent services must call business services through controlled tools and Feign clients. They must not directly access other service databases.
