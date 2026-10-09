# 学习智能体系统（本科Java方向个性化学习）· MVP

面向本科软件工程专业（Java 方向）的个性化学习智能体系统：基于 DeepSeek 大模型的多 Agent 协作，实现「诊断 → 规划 → 讲解 → 陪练 → 反馈」学习闭环。

## 一、技术栈

| 层 | 技术 |
| --- | --- |
| 前端 | 原生 HTML/CSS/JS（三栏 Agent 工作台，SSE 流式渲染，移动端响应式） |
| 后端 | Spring Boot 3.3 + Spring Security(JWT) + MyBatis-Plus + JDK 21（虚拟线程） |
| 数据库 | MySQL 5.7（20 张表，内置轻量迁移执行器 `DbMigrationRunner`） |
| AI | DeepSeek Chat API（OpenAI 兼容接口，SSE 流式 + 非流式双通道，无 Key 自动降级 Mock） |

## 二、快速启动

### 环境要求
- JDK 21+、MySQL 5.7+（本机可连）、（可选）DeepSeek API Key

### 步骤

```bash
# 1. 创建数据库（只需建库，表结构和种子数据启动时自动迁移）
mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS learning_agent DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci"

# 2. 配置（二选一）
#    a) 环境变量
set DB_PASSWORD=你的MySQL密码
set DEEPSEEK_API_KEY=sk-xxxx        # 可选，不配置则使用离线演示模式
set JWT_SECRET=一个至少32字节的随机串  # 可选，有默认开发值
#    b) 直接修改 backend/src/main/resources/application.yml

# 3. 构建并启动（Maven 由 wrapper 提供，也可用本目录的 .tools/maven）
cd backend
mvnw.cmd spring-boot:run
#   或打包运行（前端会自动打进 jar 的 static/）
mvnw.cmd package -DskipTests
java -jar target/learning-agent-backend-0.1.0-SNAPSHOT.jar

# 4. 访问
#    系统:     http://localhost:8080
#    API 文档:  http://localhost:8080/swagger-ui.html
```

> 无 DeepSeek Key 时系统以「离线演示模式」运行：对话走规则引擎、AI 批改返回模拟评分，全部业务闭环可用。配置 Key 后重启即接入真实模型。

### 演示账号（密码均为 `123456`）

| 账号 | 角色 | 说明 |
| --- | --- | --- |
| `student` | 学生（张明） | 完整学生闭环：会话/图谱/作业/练习/报告 |
| `teacher` | 教师（陈老师） | 学情看板/作业管理/提交批改/预警/学情报告 |
| `s02`~`s12` | 学生 | 12 名种子学生（供教师端学情展示） |

## 三、核心功能（MVP 交付）

**学生端 5 大模块**
1. **智能辅导对话**：DeepSeek 流式输出（SSE），6 Agent 编排（诊断/规划/辅导/代码陪练/批改/学情），知识点上下文自动注入，快捷指令芯片
2. **个性化学习路径推荐**：依赖链溯源定位根因（如 集合 42 分 ← 泛型 35 分）→ 生成 4 步路径（诊断定位/根因精讲/代码陪练/巩固测验）
3. **练习与即时评测**：37 题题库（覆盖教学大纲 11 单元），提交即判分 → 掌握度回写 → 错题本 → SM-2 复习入队（单事务）
4. **学习进度追踪与报告**：连续学习天数/周时长/平均掌握度/掌握度趋势/最薄弱 TOP3
5. **SM-2 间隔重复复习队列**：答错自动入队、复习完成按质量顺延、每日 02:00 自动补充薄弱知识点

**教师端 5 大模块**
- 学情看板（4 KPI + 卡点知识点 TOP6 + 重点关注学生）、作业发布（Rubric 模板）、提交批改（AI 初评 + Rubric 人工复核 + 反馈回流学生）、预警中心（6 类预警）、班级薄弱点报告（学情 Agent）

**教学边界（产品设计红线）**
- 严禁直接生成完整作业代码：系统提示词铁律 + 输出侧护栏（检测到 ≥15 行完整实现自动替换为含 TODO 骨架）
- 代码陪练只给三级渐进提示（思路 → 定位 → 关键结论），提示使用次数落库

## 四、API 概览（完整文档见 Swagger UI）

统一前缀 `/api`，响应格式 `{code, msg, data}`（code=0 成功），鉴权 `Authorization: Bearer <JWT>`。

| 模块 | 端点 |
| --- | --- |
| 认证 | `POST /auth/register` `POST /auth/login` `GET /auth/me` |
| 知识点 | `GET /knowledge/tree` `GET /knowledge/{code}` `GET /knowledge/{code}/trace-chain` |
| 会话 | `GET/POST /sessions` `GET /sessions/{id}` `POST /sessions/{id}/replan` `POST /sessions/{id}/steps/{n}/start` `POST /sessions/{id}/steps/{n}/complete` |
| 对话(SSE) | `POST /sessions/{id}/chat`（事件：`meta/delta/card/replace/done/error`） |
| 练习 | `GET /exercises?kpId=` `POST /exercises/{id}/attempt` `POST /exercises/{id}/hint` `GET /exercise-records` `GET /wrong-book` |
| 复习 | `GET /review-queue/today` `GET /review-queue/all` `POST /review-queue/{id}/complete` |
| 报告 | `GET /reports/overview` `GET /reports/mastery` |
| 作业(学生) | `GET /assignments/my` `POST /assignments/{id}/submissions` |
| 通知 | `GET /notifications` `PUT /notifications/{id}/read` |
| 教师端 | `GET /teacher/dashboard|classes|students|assignments|assignments/{id}/submissions|warnings` · `POST /teacher/assignments` `POST /teacher/submissions/{id}/ai-grade` `POST /teacher/assignments/{id}/ai-grade-batch` `POST /teacher/submissions/{id}/grade` `POST /teacher/insight/run` · `PUT /teacher/warnings/{id}/handle` `POST /teacher/warnings/{id}/notify` |

## 五、数据库

20 张表，启动时由 `DbMigrationRunner` 按 `classpath:db/migration/V*.sql` 版本号升序自动迁移（`schema_version` 表记录进度）：

- V1 全量建表（user/class/knowledge_point/kp_dependency/mastery/session/session_step/message/exercise/exercise_record/wrong_book/review_schedule/daily_activity/assignment/rubric/submission/rubric_score/warning/notification）
- V2 种子用户（1 教师 + 12 学生，BCrypt）
- V3 知识点树（5 一级模块 + 27 二级知识点，来自《智能Java编程-课程知识点.xlsx》，含教学大纲 11 单元映射与前置依赖）
- V4 题库（37 题，含三级渐进提示；来自 demo 题目 + 教学大纲课后作业改编）
- V5 作业/提交/预警种子

> 重新初始化演示数据：`DROP DATABASE learning_agent` 后重启。

## 六、配置项

```yaml
spring.datasource.url / username / password   # 数据库连接（环境变量 DB_USER / DB_PASSWORD）
deepseek.api-key        # DeepSeek Key（优先从 application-secret.yml 加载，其次环境变量 DEEPSEEK_API_KEY，均空则 Mock 模式）
deepseek.base-url       # 默认 https://api.deepseek.com，兼容其他 OpenAI 风格网关
deepseek.model          # 当前 deepseek-flash（混合推理模型，思维链已在客户端过滤不外泄）
deepseek.force-mock     # true 强制离线演示模式
deepseek.grade-temperature  # 批改任务固定 0.0（评分一致性）
jwt.secret / jwt.expire-hours  # JWT 密钥与有效期（默认 12h）
```

### 机密配置（API Key）

API Key 通过仓库外的 `backend/application-secret.yml` 加载（已列入 `.gitignore`，**严禁提交或外传**），由 `spring.config.import: optional:file:./application-secret.yml` 在启动时引入——因此**必须从 `backend/` 目录启动应用**。也可以改用环境变量 `DEEPSEEK_API_KEY` 传递。每次调用的模型、耗时、Token 用量、错误均记录在应用日志（`[DeepSeek]` 前缀），日志中不包含密钥。

### 模型说明

当前接入 `deepseek-flash`（混合推理模型，响应含思维链 `reasoning_content`）。客户端已做适配：流式转发时仅输出正文 `content`，思维链块只计数记录不外泄；批改等非流式任务自动取最终答案。若需更换模型（如 `deepseek-v4-pro`），修改 `application-secret.yml` 中的 `deepseek.model` 后重启即可。

## 七、100 并发保障设计

- `spring.threads.virtual.enabled=true`：JDK 21 虚拟线程，SSE 长连接不占平台线程
- HikariCP 连接池 max=30 / min-idle=10 / connection-timeout=3s
- SSE：120s 超时 + 完成回调注销 Emitter；每用户 LLM 并发信号量（2）
- DeepSeek：WebClient 非阻塞（connect 5s / read 90s），非流式 429/5xx 指数退避重试 2 次
- 批量 AI 初评走独立线程池（4/8/队列100/CallerRuns），不占请求线程
- 教师看板聚合 SQL 全部命中联合索引

## 八、测试

```bash
cd backend
mvnw.cmd test     # 14 个单元测试：SM-2 算法 / 教学边界护栏 / 路径规划算法
```

已完成的端到端验证：登录注册 → 学生闭环（建会话/诊断/SSE 流式对话/出题/答题评测/掌握度回写/错题本/复习队列/报告/作业提交/批改反馈回流）→ 教师闭环（看板/批改发布/预警处理/学情报告）。

## 九、目录结构

```
backend/
├── pom.xml / mvnw.cmd            # Maven 工程（wrapper）
├── src/main/java/com/la/
│   ├── controller/  # REST 端点（含 SSE ChatController）
│   ├── service/     # 业务（含 PathPlanService 路径推荐、GradingService AI 批改）
│   ├── ai/          # DeepSeek 客户端、Agent 提示词、意图路由、教学护栏、Mock 降级
│   ├── security/    # JWT 认证
│   ├── config/      # Security/AI/迁移执行器配置
│   ├── entity/ mapper/ dto/ exception/ util/
├── src/main/resources/
│   ├── application.yml
│   ├── prompts/     # 6 个 Agent 系统提示词（可热编辑）
│   └── db/migration/  # V1~V5 SQL
└── src/test/java/   # 单元测试

learning-agent-demo/               # 前端源码（构建时拷入 jar 的 static/）
├── login.html                     # 登录/注册页
├── index.html                     # 工作台
└── assets/js/  app.js(主逻辑) api.js(REST+SSE) auth.js(令牌) mock-data.js(静态展示数据)
```
