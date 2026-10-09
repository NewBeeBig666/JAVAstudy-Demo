-- =============================================================
-- V1: 学习智能体系统全量建表（MySQL 5.7, InnoDB, utf8mb4）
-- =============================================================

-- ---------- 用户与班级 ----------
CREATE TABLE `user` (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  username      VARCHAR(50)  NOT NULL COMMENT '登录名',
  password_hash VARCHAR(100) NOT NULL COMMENT 'BCrypt',
  real_name     VARCHAR(50)  NOT NULL,
  role          VARCHAR(20)  NOT NULL DEFAULT 'STUDENT' COMMENT 'STUDENT/TEACHER',
  student_no    VARCHAR(30)  NULL COMMENT '学号',
  class_id      BIGINT       NULL,
  created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_login_at DATETIME     NULL COMMENT '未登录预警用',
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username),
  KEY idx_class (class_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户';

CREATE TABLE `class` (
  id         BIGINT       NOT NULL AUTO_INCREMENT,
  name       VARCHAR(100) NOT NULL,
  grade      VARCHAR(20)  NULL,
  teacher_id BIGINT       NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='班级';

-- ---------- 知识点体系 ----------
CREATE TABLE `knowledge_point` (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  kp_code     VARCHAR(50)  NOT NULL COMMENT '对外字符串ID',
  name        VARCHAR(100) NOT NULL,
  parent_id   BIGINT       NULL COMMENT '一级=NULL',
  level       TINYINT      NOT NULL DEFAULT 2 COMMENT '1一级/2二级',
  unit_no     INT          NULL COMMENT '教学大纲单元1-11',
  description TEXT         NULL,
  resources   JSON         NULL,
  sort_order  INT          NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_kp_code (kp_code),
  KEY idx_parent (parent_id),
  KEY idx_unit (unit_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='知识点（两级树）';

CREATE TABLE `kp_dependency` (
  id        BIGINT      NOT NULL AUTO_INCREMENT,
  kp_id     BIGINT      NOT NULL,
  dep_kp_id BIGINT      NOT NULL COMMENT '前置知识点',
  dep_type  VARCHAR(10) NOT NULL DEFAULT 'PRE',
  PRIMARY KEY (id),
  UNIQUE KEY uk_pair (kp_id, dep_kp_id),
  KEY idx_dep (dep_kp_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='知识点依赖';

CREATE TABLE `mastery` (
  id            BIGINT   NOT NULL AUTO_INCREMENT,
  user_id       BIGINT   NOT NULL,
  kp_id         BIGINT   NOT NULL,
  mastery_value TINYINT  NOT NULL DEFAULT 0 COMMENT '0-100',
  updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_kp (user_id, kp_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='掌握度';

-- ---------- 学习会话与消息 ----------
CREATE TABLE `session` (
  id         BIGINT       NOT NULL AUTO_INCREMENT,
  user_id    BIGINT       NOT NULL,
  title      VARCHAR(200) NOT NULL,
  kp_id      BIGINT       NOT NULL COMMENT '目标知识点',
  status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/DONE',
  step_index INT          NOT NULL DEFAULT 0,
  created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_user (user_id, updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学习会话';

CREATE TABLE `session_step` (
  id           BIGINT       NOT NULL AUTO_INCREMENT,
  session_id   BIGINT       NOT NULL,
  sort_order   INT          NOT NULL DEFAULT 1,
  title        VARCHAR(200) NOT NULL,
  description  VARCHAR(500) NULL,
  duration_min INT          NOT NULL DEFAULT 10,
  status       VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/ACTIVE/DONE',
  PRIMARY KEY (id),
  KEY idx_session (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会话步骤';

CREATE TABLE `message` (
  id          BIGINT      NOT NULL AUTO_INCREMENT,
  session_id  BIGINT      NOT NULL,
  sender      VARCHAR(10) NOT NULL COMMENT 'SYS/USER/AGENT',
  agent_type  VARCHAR(20) NULL COMMENT 'diagnosis/planning/tutor/code/grading/insight',
  content     MEDIUMTEXT  NULL,
  card_type   VARCHAR(30) NULL,
  card_payload JSON       NULL,
  created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_session (session_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会话消息';

-- ---------- 练习与复习 ----------
CREATE TABLE `exercise` (
  id           BIGINT      NOT NULL AUTO_INCREMENT,
  kp_id        BIGINT      NOT NULL,
  type         VARCHAR(10) NOT NULL DEFAULT 'CHOICE' COMMENT 'CHOICE/CODE(代码阅读选择题)',
  difficulty   VARCHAR(10) NOT NULL DEFAULT '中等' COMMENT '简单/中等/困难',
  stem         TEXT        NOT NULL,
  code         TEXT        NULL,
  options      JSON        NOT NULL,
  answer       TINYINT     NOT NULL,
  analysis     TEXT        NULL,
  hints        JSON        NULL COMMENT '三级渐进提示',
  mastery_delta TINYINT    NOT NULL DEFAULT 5,
  PRIMARY KEY (id),
  KEY idx_kp (kp_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='练习题';

CREATE TABLE `exercise_record` (
  id          BIGINT   NOT NULL AUTO_INCREMENT,
  user_id     BIGINT   NOT NULL,
  exercise_id BIGINT   NOT NULL,
  kp_id       BIGINT   NOT NULL,
  selected    TINYINT  NULL,
  is_correct  TINYINT  NOT NULL DEFAULT 0,
  delta       SMALLINT NOT NULL DEFAULT 0,
  hint_used   TINYINT  NOT NULL DEFAULT 0 COMMENT '本局使用的最高提示级数',
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_user_kp (user_id, kp_id),
  KEY idx_user_time (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='练习记录';

CREATE TABLE `wrong_book` (
  id            BIGINT   NOT NULL AUTO_INCREMENT,
  user_id       BIGINT   NOT NULL,
  exercise_id   BIGINT   NOT NULL,
  kp_id         BIGINT   NOT NULL,
  wrong_count   INT      NOT NULL DEFAULT 1,
  last_wrong_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_ex (user_id, exercise_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='错题本';

CREATE TABLE `review_schedule` (
  id                BIGINT        NOT NULL AUTO_INCREMENT,
  user_id           BIGINT        NOT NULL,
  exercise_id       BIGINT        NOT NULL,
  kp_id             BIGINT        NOT NULL,
  title             VARCHAR(300)  NOT NULL,
  ease_factor       DECIMAL(3,2)  NOT NULL DEFAULT 2.50 COMMENT 'SM-2 EF',
  interval_days     INT           NOT NULL DEFAULT 1,
  repetitions       INT           NOT NULL DEFAULT 0,
  next_review_date  DATE          NOT NULL,
  last_review_date  DATE          NULL,
  source            VARCHAR(10)   NOT NULL DEFAULT 'WRONG' COMMENT 'WRONG/WEAK',
  status            VARCHAR(10)   NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/DONE',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_ex (user_id, exercise_id),
  KEY idx_user_next (user_id, next_review_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='SM-2复习队列';

CREATE TABLE `daily_activity` (
  id        BIGINT   NOT NULL AUTO_INCREMENT,
  user_id   BIGINT   NOT NULL,
  stat_date DATE     NOT NULL,
  minutes   INT      NOT NULL DEFAULT 0 COMMENT '当日学习分钟数',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_date (user_id, stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='每日活跃';

-- ---------- 作业与批改 ----------
CREATE TABLE `assignment` (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  teacher_id  BIGINT       NOT NULL,
  class_id    BIGINT       NOT NULL,
  title       VARCHAR(200) NOT NULL,
  description TEXT         NULL,
  kp_ids      JSON         NULL,
  due_at      DATETIME     NULL,
  status      VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/ONGOING/CLOSED',
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_class_status (class_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='作业';

CREATE TABLE `rubric` (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  assignment_id BIGINT       NOT NULL,
  name          VARCHAR(100) NOT NULL,
  max_score     INT          NOT NULL DEFAULT 20,
  description   VARCHAR(300) NULL,
  sort_order    INT          NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  KEY idx_assignment (assignment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='评分标准';

CREATE TABLE `submission` (
  id              BIGINT      NOT NULL AUTO_INCREMENT,
  assignment_id   BIGINT      NOT NULL,
  student_id      BIGINT      NOT NULL,
  code            MEDIUMTEXT  NULL,
  status          VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'MISSING/PENDING/AI_REVIEWED/GRADED',
  attempts        INT         NOT NULL DEFAULT 0,
  similarity      TINYINT     NOT NULL DEFAULT 0,
  ai_score        SMALLINT    NULL,
  ai_comment      TEXT        NULL,
  teacher_comment TEXT        NULL,
  score           SMALLINT    NULL,
  submitted_at    DATETIME    NULL,
  graded_at       DATETIME    NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_as_stu (assignment_id, student_id),
  KEY idx_as_status (assignment_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生提交';

CREATE TABLE `rubric_score` (
  id            BIGINT   NOT NULL AUTO_INCREMENT,
  submission_id BIGINT   NOT NULL,
  rubric_id     BIGINT   NOT NULL,
  score         SMALLINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_sub_rubric (submission_id, rubric_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Rubric逐项得分';

-- ---------- 预警与通知 ----------
CREATE TABLE `warning` (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  class_id    BIGINT       NOT NULL,
  student_id  BIGINT       NULL COMMENT 'NULL=班级共性',
  level       VARCHAR(10)  NOT NULL DEFAULT 'MID' COMMENT 'HIGH/MID',
  type        VARCHAR(50)  NOT NULL,
  description VARCHAR(500) NOT NULL,
  handled     TINYINT      NOT NULL DEFAULT 0,
  handled_at  DATETIME     NULL,
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_class_handled (class_id, handled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='预警';

CREATE TABLE `notification` (
  id         BIGINT       NOT NULL AUTO_INCREMENT,
  user_id    BIGINT       NOT NULL,
  content    VARCHAR(500) NOT NULL,
  type       VARCHAR(30)  NOT NULL DEFAULT 'SYSTEM',
  read_flag  TINYINT      NOT NULL DEFAULT 0,
  created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_user_read (user_id, read_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='通知';
