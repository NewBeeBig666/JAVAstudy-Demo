# 学习智能体系统（本科Java方向个性化学习）

面向本科软件工程专业（Java 方向）的个性化学习智能体系统：基于 DeepSeek 大模型的多 Agent 协作，实现「诊断 → 规划 → 讲解 → 陪练 → 反馈」学习闭环。

## 项目结构

| 目录 | 说明 |
| --- | --- |
| `backend/` | Spring Boot 3 后端（REST API + SSE 流式对话 + MySQL + DeepSeek 集成），详见 [backend/README.md](backend/README.md) |
| `learning-agent-demo/` | 前端（三栏 Agent 工作台，构建时自动打入后端 jar 的 static/） |
| `学习智能体系统_产品设计文档.docx` | 产品设计文档 |

## 快速启动

```bash
# 1. 创建数据库
mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS learning_agent DEFAULT CHARACTER SET utf8mb4"

# 2. 构建并启动（前端自动打进 jar）
cd backend
mvnw.cmd clean package -DskipTests
java -jar target/learning-agent-backend-0.1.0-SNAPSHOT.jar

# 3. 访问 http://localhost:8080（演示账号见 backend/README.md）
```

> DeepSeek API Key 等机密配置通过仓库外的 `backend/application-secret.yml` 或环境变量注入，未配置时自动降级为离线演示模式。

## 核心功能

- **学生端**：智能辅导对话（SSE 流式）、个性化学习路径推荐（依赖链溯源）、练习与即时评测（掌握度回写 + 错题本）、SM-2 间隔重复复习、学习报告
- **教师端**：学情看板、作业发布（Rubric 驱动）、AI 辅助批改、预警中心、班级名单导入（Excel/Word）、学情报告
- **教学边界**：严禁直接生成完整作业代码，代码陪练仅提供三级渐进提示（思路 → 定位 → 关键结论）

技术栈与部署细节见 [backend/README.md](backend/README.md)。
