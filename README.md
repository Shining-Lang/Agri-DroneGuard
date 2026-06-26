# Agri UAV Agent

Agri UAV Agent 是一个面向农业飞防场景的 AI 应用工程项目，用于演示“Java 后端 + Python Agent + GIS 风险校验 + 人工审批 + 模拟任务下发”的完整工程闭环。

项目目标不是控制真实无人机，而是构建一个可上线演示、可写入简历、可在面试中讲清楚架构取舍的 AI Agent 应用系统。

## 项目定位

本项目模拟县级农服中心/飞防大队的无人机打药任务流程：

```text
种植大户提交托管打药申请
-> Java 后端校验地块归属并创建任务
-> Redis Stream 投递 Agent 规划任务
-> Python Agent 生成航线方案与风险评估
-> Java 后端持久化规划结果并触发审批
-> 管理员对高风险任务进行人工确认
-> 系统生成模拟无人机飞控任务包
```

## 项目边界

本项目属于演示级/准生产级工程项目，明确不做以下内容：

- 不直接控制真实无人机。
- 不提供真实农药合规建议。
- 不接入真实政务审批系统。
- 不使用真实村集体土地数据。
- 不自动执行高风险低空飞行任务。

项目会通过模拟数据、模拟审批和模拟飞控任务包，展示完整的工程设计能力。

## 架构分工

```text
frontend/  Vue3 管理端页面
backend/   Spring Boot 业务后端
agent/     FastAPI + LangGraph 智能规划服务
infra/     Docker Compose 基础设施
docs/      技术选型、ADR、深入设计文档
scripts/   本地开发和演示辅助脚本
```

核心原则：

- Java 后端负责可信业务底座：订单、审批、审计、状态流转、任务下发。
- Python Agent 负责智能规划：RAG、航线规划、风险评估、工具调用。
- GIS/确定性规则负责安全边界：地块越界、禁飞区、高压线等风险校验。
- 人工审批负责高风险兜底：Agent 只给建议，不直接执行高风险任务。

## 第一阶段目标

第一阶段先完成项目骨架和本地运行环境：

1. 使用 Docker Compose 启动 PostgreSQL/PostGIS 和 Redis。
2. 启动 Spring Boot 后端，并提供健康检查接口。
3. 启动 FastAPI Agent 服务，并提供健康检查接口。
4. 启动 Vue3 前端，展示项目管理台雏形。
5. 在 `docs/` 中持续记录技术选型、ADR 和深入设计。

## 技术栈

| 模块 | 技术 |
| --- | --- |
| 前端 | Vue3, Vite, TypeScript |
| Java 后端 | Java 21, Spring Boot 3, Maven |
| Python Agent | Python, FastAPI, LangGraph, requirements.txt |
| 数据库 | PostgreSQL, PostGIS |
| 队列/缓存 | Redis, Redis Stream |
| GIS/算法 | GeoJSON, PostGIS, Shapely |
| 部署 | Docker Compose |
| 文档 | Markdown, ADR, deep-dive 技术专题 |

## 文档体系

```text
docs/
  tech-selection-handbook.md  技术选型总手册
  adr/                        架构决策记录
  deep-dive/                  技术深入专题
  roadmap.md                  项目路线图
```

每个关键技术栈都会围绕以下问题进行记录：

1. 当前业务问题是什么？
2. 技术方案需要满足什么约束？
3. 可选方案有哪些？
4. 为什么最终选择当前方案？
5. 替代方案的问题在哪里？
6. 极端场景下当前方案如何表现？
7. 当前方案的代价是什么？

## 本地运行计划

后续将按以下顺序逐步跑通：

```text
infra -> backend -> agent -> frontend -> end-to-end demo
```

当前仓库已经完成第一版目录骨架，后续会逐步补齐业务模型、数据库表、Agent 工作流、审批流和模拟执行流程。
