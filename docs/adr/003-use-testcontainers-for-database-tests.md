# ADR-003：使用 Testcontainers 验证数据库迁移

## 状态

已采纳。

## 当前业务问题

项目依赖 PostgreSQL/PostGIS 的数据类型、约束和索引。数据库迁移测试需要尽可能接近真实运行环境，同时不能污染开发者本地的长期数据库。

## 技术约束

- 测试必须能够重复运行并保持相互隔离。
- 测试结束后应自动清理数据库容器。
- 后续迁移会使用 PostGIS 和 PostgreSQL 专用能力。
- 本地和 CI 环境需要安装可用的 Docker Engine。

## 可选方案

- H2 内存数据库：启动快，但与 PostgreSQL/PostGIS 的行为存在差异。
- 共享 Docker Compose 数据库：接近生产，但测试之间容易相互污染，也依赖人工启动和清理。
- Testcontainers：每次测试按需创建临时真实数据库，并在结束后自动清理。

## 最终选择

数据库迁移和持久化集成测试使用 Testcontainers，并运行与本地环境一致的 `postgis/postgis:16-3.4` 镜像。

## 选择理由

Testcontainers 能验证真实 PostgreSQL 方言、Flyway 行为和未来的 PostGIS 空间能力。测试拥有独立数据库和随机端口，不依赖开发者手工准备测试数据，也不会修改长期运行的本地数据库。

## 替代方案的问题

H2 无法可靠覆盖空间类型、PostgreSQL 约束语义和专用 SQL。共享数据库容易产生测试顺序依赖，并且并行执行时可能发生数据冲突。

## 极端场景表现

并行测试可以使用不同容器隔离数据。即使测试进程异常退出，Ryuk 也会尝试回收临时资源。Docker 不可用时，数据库集成测试会明确失败，而不是悄悄切换到行为不同的内存数据库。

## 代价与风险

- 首次运行需要下载镜像，速度慢于内存数据库。
- 测试依赖 Docker，CI 需要提供容器运行环境。
- 容器会占用额外的 CPU、内存和磁盘空间。
- Docker Engine 提升最低 API 版本时，旧版 Testcontainers 可能需要升级。

## 项目落点

- 测试依赖：`backend/pom.xml`
- 迁移测试：`backend/src/test/java/com/agriuav/backend/AgriUavBackendApplicationTests.java`
- 测试镜像：`postgis/postgis:16-3.4`

## 面试表达

我没有使用 H2 验证 PostgreSQL/PostGIS 迁移，因为内存数据库无法覆盖真实方言和空间能力。我使用 Testcontainers 为每次测试创建隔离的 PostGIS 数据库，验证完成后自动回收，代价是测试更慢并依赖 Docker。
