# ADR-002：使用 Flyway 管理数据库迁移

## 状态

已采纳。

## 当前业务问题

打药申请、地块、审批和航线等数据结构会持续演进。开发环境、自动化测试和未来线上环境必须以相同顺序创建数据库结构，并且能够追踪每一次结构变化。

## 技术约束

- 主数据库是 PostgreSQL/PostGIS。
- Java 服务是业务数据结构的主要维护者。
- 迁移需要进入 Git，能够被审查和复现。
- 后续会包含空间字段、索引和 PostgreSQL 专用 SQL。

## 可选方案

- Hibernate `ddl-auto`：根据实体自动创建或更新表结构。
- Liquibase：使用 XML、YAML、JSON 或 SQL 描述变更集。
- Flyway：按版本顺序执行显式 SQL 迁移。

## 最终选择

使用 Flyway，并将版本化 SQL 放在 `backend/src/main/resources/db/migration`。生产代码不会使用 Hibernate `ddl-auto=update` 修改数据库结构。

## 选择理由

Flyway 的迁移模型简单，SQL 可以直接表达 PostGIS 类型、空间索引、约束和 PostgreSQL 特性。迁移文件进入 Git 后，数据库结构变化可以像代码一样被审查，并由应用启动流程自动校验。

## 替代方案的问题

Hibernate `ddl-auto` 适合原型，但自动推断的结构变化不够可控，难以审查高风险删除或类型修改。Liquibase 功能更全面，但变更集语法和维护成本更高；当前个人项目不需要复杂的跨数据库抽象。

## 极端场景表现

当多实例同时启动时，Flyway 使用数据库锁避免重复迁移。当迁移校验失败时，应用应拒绝启动，避免新代码运行在旧结构上。大表变更仍需拆分为兼容性迁移，Flyway 本身不能消除长时间锁表风险。

## 代价与风险

- 已执行的迁移不能随意修改，只能新增修复迁移。
- PostgreSQL 专用 SQL 会降低跨数据库迁移能力。
- 复杂迁移需要人工设计回滚和数据修复方案。

## 项目落点

- 数据源配置：`backend/src/main/resources/application.yml`
- 迁移脚本：`backend/src/main/resources/db/migration`
- 迁移验证：后端 Testcontainers 集成测试

## 面试表达

我选择 Flyway 是因为项目后续包含 PostGIS 类型、空间索引和审批审计数据，需要显式、可审查、可复现的数据库变更。相比 Hibernate 自动建表，Flyway 在多环境一致性和失败阻断方面更可靠；相比 Liquibase，它更符合当前以 PostgreSQL SQL 为主的项目规模。
