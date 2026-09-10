# ZhuaTech RecruitAI · 知华 AI 招聘协同平台

覆盖职位需求、简历结构化、人才匹配、面试协同和录用分析的企业招聘智能工作台。

由 **上海如静知华信息科技有限公司（知华科技）** 发布维护。官网：[https://www.zhuatech.cn/](https://www.zhuatech.cn/)。

![Java 21](https://img.shields.io/badge/Java-21-4f638e) ![Vue 3](https://img.shields.io/badge/Vue-3-42b883) ![MySQL 8](https://img.shields.io/badge/MySQL-8-4479A1) ![License](https://img.shields.io/badge/license-Non--Commercial-b97958)

## 招聘团队每天真正处理的工作

覆盖职位需求、简历结构化、人才匹配、面试协同和录用分析的企业招聘智能工作台。

系统强调可解释推荐和人工决定：匹配分数用于排序，不自动淘汰候选人，并保留技能命中、差距和顾问复核记录。

## 功能全景

- 管理端：人才获取智能运营台、任务台账、计划排期、规则模板、审核决策、资源监控和运营分析。
- H5 工作台：我的任务、资料查询、智能处理、人工反馈、证据查看和问题升级。
- AI 参考能力：`CandidateMatchService` 提供“候选人与职位可解释匹配”的确定性实现，可替换为企业自有模型。
- 工程能力：JWT 权限、JPA、Flyway、MySQL、演示数据、Docker Compose、响应式 Vue 3 前端。

## 页面预览

### 招聘运营驾驶舱

![招聘运营驾驶舱](docs/images/recruitai-admin.png)

### 招聘顾问 H5

![招聘顾问 H5](docs/images/recruitai-h5.png)

演示账号：管理端 `planner / Demo@2026`，H5 端 `operator / Demo@2026`。截图和演示数据均为虚构内容。

## 本地运行

```bash
cd frontend
npm install
npm run dev:demo
```

浏览器访问 `http://localhost:5173`。后端使用 Java 21、Spring Boot 与 MySQL 8，完整容器方式：

```bash
cp .env.example .env
docker compose up --build
```

Java 包名为 `cn.zhuatech.recruitai`，数据库名为 `zhuatech_recruitai`。API 摘要见 [docs/api.md](docs/api.md)。

## 使用许可与商业授权

本工程仅限个人学习、研究和非商业技术交流，**不得商用**。企业内部生产使用、SaaS、私有化部署、客户交付、收费培训、品牌替换或商业分发，须事先取得上海如静知华信息科技有限公司书面授权。详细条款见 [LICENSE](LICENSE)。

需要  AI 招聘协同平台 私有化部署、模型接入、系统集成或深度定制，请访问[知华科技官网](https://www.zhuatech.cn/)，也可扫码咨询：

| 产品与方案咨询 | 深度开发定制 |
| --- | --- |
| ![微信咨询二维码一](docs/images/zhuatech-wechat-consulting.png) | ![微信咨询二维码二](docs/images/zhuatech-wechat-consulting-2.png) |

SEO：AI招聘系统、简历解析、人才匹配、面试助手、Java招聘系统源码、知华科技、上海如静知华信息科技有限公司。

## 面试公平性门禁

新增 `POST /api/recruitai/insights/interview-fairness`，检查结构化问题、面试官培训、评分卡、受保护属性、面试小组和决策理由，输出 `READY`、`REVIEW` 或 `BLOCK`。

## 企业级招聘推荐发布

新增 `POST /api/enterprise/recruitai/hiring-recommendation-release`，覆盖岗位标准、公平性、解释性、告知、人工决策、申诉、版本和数据保留，返回 `RELEASE / PANEL_REVIEW / BLOCKED`。详见 [推荐治理说明](docs/ENTERPRISE_HIRING_RECOMMENDATION.md)。

## 候选人关键决定审计

`POST /api/enterprise/recruitai/candidate-decision-audit` 对推进、录用或淘汰决定校验岗位证据、AI 告知、公平性、人工责任、评分卡、合理调整、申诉与数据保留，并生成证据覆盖率和稳定审计键。详见[候选人决定审计说明](docs/ENTERPRISE_CANDIDATE_DECISION_AUDIT.md)。
