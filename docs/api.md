# RECRUITAI API 摘要

版权所有 © 2026 上海如静知华信息科技有限公司。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/login` | 登录并获取 JWT |
| GET | `/api/admin/dashboard` | 人才获取智能运营台 |
| GET | `/api/admin/work-orders` | 招聘任务列表 |
| GET | `/api/shopfloor/dashboard` | 招聘顾问工作台 |
| POST | `/api/shopfloor/work-orders/{id}/reports` | 提交处理反馈 |
| POST | `/api/ai/candidate/match` | 候选人与职位可解释匹配 |
| POST | `/api/shopfloor/ai-risk-assessment` | AI 功能上线风险初筛 |
| POST | `/api/enterprise/recruitai/hiring-recommendation-release` | 校验招聘推荐发布治理条件 |
| POST | `/api/enterprise/recruitai/candidate-decision-audit` | 审计人工招聘决定、岗位证据与公平性 |

除登录外均需 `Authorization: Bearer <token>`。社区演示实现不调用外部模型，不需要 API Key。
