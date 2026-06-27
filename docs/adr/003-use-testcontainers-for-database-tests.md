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

## 现实工程场景

### 场景一：Docker Engine 升级后测试突然失败

- 背景：项目最初由 Spring Boot 3.3.6 管理 Testcontainers `1.19.8`，本机后来使用 Docker Engine 29。
- 触发条件：旧版 docker-java 客户端使用 API `1.32`，而当前 Docker Engine 已不再接受该版本。
- 系统表现：`mvn test` 在创建 PostGIS 容器前返回 HTTP 400，并报告找不到有效 Docker 环境；业务代码和迁移 SQL尚未开始执行。
- 处置方案：检查 `docker version` 与 Maven 依赖树，确认是客户端 API 兼容问题后，将 Testcontainers 固定到兼容近期 Docker Engine 的 `1.21.4`，再重新运行测试。
- 预防规则：固定 Testcontainers 版本；Docker Desktop 或 CI 镜像升级后，先运行数据库集成测试；不要通过关闭测试或改用 H2 掩盖兼容问题。
- 本项目落点：`backend/pom.xml` 显式设置 `testcontainers.version`，测试日志需要打印 Testcontainers 和 Docker Server 版本。

### 场景二：CI 没有可用的 Docker

- 背景：某些 CI 执行器禁止访问 Docker socket，或者 Docker 服务尚未启动。
- 触发条件：数据库集成测试尝试创建临时 PostGIS 容器。
- 系统表现：测试在启动阶段快速失败，并明确报告无法发现 Docker 环境，不会误连开发数据库。
- 处置方案：为 CI 作业提供受控的 Docker 环境和最小权限；如果平台不能运行容器，就将数据库集成测试放到支持 Docker 的独立作业，而不是切换成语义不同的 H2 测试。
- 预防规则：CI 启动时先执行 Docker 可用性检查，并把单元测试与数据库集成测试分层展示结果。
- 本项目落点：后续 GitHub Actions 工作流会在运行 `mvn test` 前验证 Docker，并缓存常用镜像层。

### 场景三：并行测试互相污染数据

- 背景：多个分支或多个测试类同时验证数据库写入和迁移。
- 触发条件：所有测试共用一个固定端口、固定 schema 的长期 PostgreSQL 实例。
- 系统表现：测试结果依赖执行顺序，可能出现唯一键冲突、脏数据和偶发失败。
- 处置方案：每个测试上下文使用独立容器、独立数据库和随机映射端口；测试数据由测试自身创建。
- 预防规则：测试不能依赖本地 Compose 数据库中的预置状态，也不能假设固定宿主机端口。
- 本项目落点：`PostgreSQLContainer` 动态提供 JDBC URL、用户名和密码，并通过 `DynamicPropertySource` 注入 Spring Boot。

### 场景四：测试进程中断后残留容器

- 背景：开发者强制终止 Maven、IDE 崩溃或 CI 作业被取消。
- 触发条件：JVM 没有机会执行正常的测试清理逻辑。
- 系统表现：临时数据库可能继续占用内存、磁盘和随机端口，长期积累会拖慢开发机或 CI 节点。
- 处置方案：保留 Ryuk 资源回收机制；CI 作业结束时额外检查带有 Testcontainers 标签的残留资源，并按平台策略清理。
- 预防规则：不随意禁用 Ryuk；容器命名、标签和生命周期必须由 Testcontainers 管理。
- 本项目落点：每次测试结束后检查运行容器，正常情况下只保留项目自己的 PostGIS 和 Redis 容器。

## 团队协作规范

- 测试镜像和 Testcontainers 库版本必须显式固定。
- 数据库集成测试使用真实 PostgreSQL/PostGIS，不静默降级到 H2。
- 测试不得连接开发、测试共享环境或生产数据库。
- 首次镜像下载慢不应被误判为测试卡死，CI 应配置合理超时和镜像缓存。
- Docker 与测试库升级需要通过迁移测试后再合并。
- 测试结束必须确认临时容器已回收。

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

## 面试追问

### Q：为什么不用 H2，让测试跑得更快？

H2 适合不依赖数据库方言的快速测试，但无法可靠验证 PostGIS 类型、PostgreSQL 锁行为、约束语义和专用 SQL。这个项目保留普通单元测试的速度，同时用 Testcontainers 覆盖真正依赖数据库的迁移与持久化测试。

### Q：Testcontainers 测试失败，如何判断是代码问题还是环境问题？

先看失败阶段。如果还没有创建业务容器，就检查 Docker 服务、socket 权限、客户端 API 和镜像拉取；如果容器已经就绪且 Flyway 执行失败，再检查 SQL、校验和和数据库版本。需要把基础设施失败与业务断言失败分开定位。

### Q：Testcontainers 的主要代价是什么？

首次拉取镜像和容器启动会增加测试时间，执行环境必须提供 Docker，并需要管理磁盘和并发资源。它换来的是更高的数据库行为真实性和测试隔离性，因此只用于需要真实依赖的集成测试，而不是替代所有单元测试。
