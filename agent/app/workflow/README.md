# Agent 工作流

本目录用于放置 LangGraph 工作流相关代码。

计划中的节点如下：

```text
receive_task
-> retrieve_agronomy_knowledge
-> calculate_pesticide_ratio
-> generate_route
-> validate_airspace
-> assess_risk
-> wait_human_approval
-> finalize_plan
```

## 节点职责

- `receive_task`：接收 Java 后端投递的规划任务。
- `retrieve_agronomy_knowledge`：检索农艺知识库。
- `calculate_pesticide_ratio`：计算或建议农药稀释比例。
- `generate_route`：调用确定性航线工具生成航点。
- `validate_airspace`：校验航线是否触碰禁飞区、边界或敏感区域。
- `assess_risk`：输出风险等级和审批建议。
- `wait_human_approval`：为高风险任务预留人工审批中断点。
- `finalize_plan`：生成最终规划结果。

## 设计原则

大模型负责解释、推理和策略建议，航线坐标生成与空间风险校验由确定性工具完成。
