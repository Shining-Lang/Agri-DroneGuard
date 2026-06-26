# 基础设施

本目录用于管理本地开发和演示环境中的基础设施。

当前使用 Docker Compose 启动：

- PostgreSQL/PostGIS
- Redis

## 启动方式

在 `infra` 目录下执行：

```powershell
docker compose --env-file ..\.env.example up -d
```

## 服务说明

| 服务 | 默认端口 | 用途 |
| --- | --- | --- |
| PostgreSQL/PostGIS | `5432` | 存储订单、地块、禁飞区、航线和审计数据 |
| Redis | `6379` | 缓存与 Redis Stream 异步任务队列 |

## 初始化说明

首次启动数据库时，会执行：

```text
postgres/init/001-enable-postgis.sql
```

该脚本用于启用 PostGIS 扩展。
