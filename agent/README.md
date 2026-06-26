# Agent 服务

本模块是 Python Agent 服务，基于 FastAPI 对外提供接口，后续会接入 LangGraph、RAG、确定性航线工具和风险评估工具。

## 本地启动

```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

启动后可以访问：

```text
http://localhost:8000/api/health
```

## 计划职责

- 从 Redis Stream 消费规划任务。
- 启动 LangGraph 工作流。
- 通过 RAG 检索农艺知识。
- 调用确定性航线生成工具。
- 调用空间风险校验工具。
- 向 Java 后端返回结构化规划结果和风险评估结果。

## 边界说明

Agent 只负责生成规划建议和风险说明，不直接控制真实无人机，也不直接越过 Java 审批流程执行高风险任务。
