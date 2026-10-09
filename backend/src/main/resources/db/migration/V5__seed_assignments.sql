-- V5: 作业/提交/预警种子（对应 demo 作业流，适配课程知识点体系）

-- ===== 作业 =====
INSERT INTO `assignment` (id, teacher_id, class_id, title, description, kp_ids, due_at, status, created_at) VALUES
(1, 1, 1, '集合框架综合练习：手写简易 HashMap',
 '基于「数组 + 链表」实现一个支持 put/get/remove 的简易 HashMap，要求处理哈希冲突（拉链法），并说明扩容思路。可用泛型提升通用性。',
 '["kp-collection","kp-generic","kp-array"]', DATE_ADD(NOW(), INTERVAL -3 DAY), 'CLOSED', DATE_SUB(NOW(), INTERVAL 7 DAY)),
(2, 1, 1, '异常处理实战：成绩校验程序',
 '编写程序接收用户输入的分数（0-100）。不在该范围内时抛出自定义异常 ScoreOutOfBoundsException 并提示；同时用 try-with-resources 管理输入资源。附测试用例说明。',
 '["kp-exception","kp-select"]', DATE_ADD(NOW(), INTERVAL 3 DAY), 'ONGOING', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 1, 1, '面向接口编程：饲养员喂食模拟（多态）',
 '使用多态模拟饲养员喂食动物：给小狗喂骨头小狗汪汪叫，给小猫喂小鱼小猫喵喵叫。要求定义 Animal 抽象类/接口，Dog 和 Cat 各自实现，饲养员类面向 Animal 编程。',
 '["kp-poly","kp-abstract","kp-interface"]', DATE_ADD(NOW(), INTERVAL 9 DAY), 'DRAFT', DATE_SUB(NOW(), INTERVAL 1 DAY));

-- ===== Rubric =====
INSERT INTO `rubric` (id, assignment_id, name, max_score, description, sort_order) VALUES
(1, 1, '功能正确性', 40, 'put/get/remove 正确，冲突处理无误', 1),
(2, 1, '边界与异常处理', 20, 'null key、容量边界、非法入参', 2),
(3, 1, '数据结构设计', 20, '拉链法结构清晰，复杂度分析到位', 3),
(4, 1, '代码规范与注释', 20, '命名、注释、无魔法数字', 4),
(5, 2, '功能正确性', 40, '校验逻辑与异常抛出正确', 1),
(6, 2, '自定义异常设计', 25, '继承结构合理，信息完整', 2),
(7, 2, '资源管理', 20, '正确使用 try-with-resources', 3),
(8, 2, '代码规范与注释', 15, '命名、注释、测试用例说明', 4),
(9, 3, '多态运用', 40, '面向抽象编程，无硬编码类型判断', 1),
(10, 3, '类结构设计', 25, '抽象类/接口设计合理', 2),
(11, 3, '可扩展性', 20, '新增动物零修改饲养员代码', 3),
(12, 3, '代码规范与注释', 15, '命名、注释、结构清晰', 4);

-- ===== 提交 =====
INSERT INTO `submission` (id, assignment_id, student_id, code, status, attempts, similarity, ai_score, ai_comment, teacher_comment, score, submitted_at, graded_at) VALUES
(1, 1, 2, 'public class SimpleHashMap<K, V> {\n    private Node<K, V>[] table;\n    private int size;\n\n    public V put(K key, V value) {\n        int idx = hash(key) % table.length;\n        // 拉链法：头插法处理冲突\n        for (Node<K, V> n = table[idx]; n != null; n = n.next) {\n            if (n.key.equals(key)) {\n                V old = n.value;\n                n.value = value;\n                return old;\n            }\n        }\n        table[idx] = new Node<>(key, value, table[idx]);\n        size++;\n        return null;\n    }\n    // get/remove 略\n}', 'GRADED', 3, 12, 80,
 '拉链法冲突处理正确，头插法使用熟练。get/remove 未实现完整；未处理 null key；扩容思路未说明。',
 '实现思路正确，链表操作熟练。建议：① 补全 get/remove 与扩容说明；② put 中 null key 需单独处理（固定桶位 0）；③ equals 比较前先判 hash 减少开销。',
 82, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 1, 3, 'public class SimpleHashMap<K, V> {\n    // 数组 + 链表，含扩容与 null 处理，附复杂度分析注释\n}', 'GRADED', 1, 9, 90,
 '结构完整，null key 固定桶 0 的处理是亮点，扩容时机与负载因子说明清晰。',
 '完成度很高，扩容部分可以在课上分享。思考：多线程下这个实现会出什么问题？（引出并发章节）',
 93, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 2, 2, 'public class ScoreValidator {\n    public void check(int score) {\n        if (score < 0 || score > 100) {\n            throw new ScoreOutOfBoundsException("分数必须在 0—100 之间");\n        }\n        System.out.println("成绩：" + score);\n    }\n}\n\nclass ScoreOutOfBoundsException extends RuntimeException {\n    public ScoreOutOfBoundsException(String msg) { super(msg); }\n}',
 'PENDING', 2, 15, 76,
 '自定义异常继承 RuntimeException 并携带信息，校验逻辑正确。try-with-resources 部分 TODO 未完成。',
 '', NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), NULL),
(4, 2, 3, '// 完整实现 + Scanner try-with-resources + JUnit 测试用例', 'PENDING', 1, 11, 88,
 'try-with-resources 使用规范，测试用例覆盖了三个边界。', '', NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), NULL),
(5, 2, 4, 'public class ScoreValidator {\n    public void check(int score) throws Exception {\n        if (score < 0 || score > 100) {\n            throw new Exception("错误");\n        }\n    }\n}',
 'PENDING', 1, 34, 52,
 '直接抛出笼统的 Exception 而非自定义异常，丢失了业务语义；throws 声明后调用方也未处理。',
 '', NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), NULL),
(6, 2, 8, 'public class ScoreValidator {\n    // 自定义异常 + 校验 + 输入循环，结构规范', 'PENDING', 2, 13, 85,
 '校验与异常设计合理，输入循环的退出条件描述清晰。', '', NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), NULL),
(7, 2, 13, '// 与 s05 提交内容高度一致，变量命名与注释结构雷同\npublic class ScoreValidator { /* ... */ }',
 'PENDING', 6, 87, 70,
 '相似度检测 87%，与刘佳怡提交高度雷同；且提交于凌晨、6 次重复提交，行为异常。',
 '', NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), NULL),
(8, 1, 7, '', 'MISSING', 0, 0, NULL, '', '', NULL, NULL, NULL),
(9, 1, 11, '', 'MISSING', 0, 0, NULL, '', '', NULL, NULL, NULL);

INSERT INTO `rubric_score` (submission_id, rubric_id, score) VALUES
(1, 1, 36), (1, 2, 13), (1, 3, 18), (1, 4, 15),
(2, 1, 40), (2, 2, 18), (2, 3, 20), (2, 4, 15);

-- ===== 预警 =====
INSERT INTO `warning` (id, class_id, student_id, level, type, description, handled, created_at) VALUES
(1, 1, 7,  'HIGH', '连续未登录', '连续 5 天未登录，作业提交率从 75% 降至 42%，近 7 日活跃趋势归零。', 0, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 1, 4,  'HIGH', '提交次数骤降', '近 3 天未登录，练习提交次数下降 80%，面向对象章节连续 4 次不通过。', 0, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 1, 11, 'MID',  '卡点知识点', '继承与多态连续 4 次练习不通过且未申请提示，判定为「卡死」而非「偷懒」。', 0, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(4, 1, 9,  'MID',  '非正常时段提交', '近 7 日有 6 次提交发生在 00:00-04:00，可能存在赶工或代做风险。', 0, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, 1, 13, 'HIGH', '代码相似度异常', '本次提交与刘佳怡相似度 87%，且 30 分钟内重复提交 6 次，建议人工复核。', 0, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(6, 1, NULL, 'MID', '班级共性卡点', '泛型班级平均掌握度低于 40，卡点集中在「类型擦除」与「通配符」，建议下节课复盘。', 0, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- ===== 通知（张明） =====
INSERT INTO `notification` (id, user_id, content, type, read_flag, created_at) VALUES
(1, 2, '作业「集合框架综合练习」已批改：82 分。查看教师评语与 Rubric 逐项得分。', 'GRADE', 0, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 2, '新作业已发布：「异常处理实战：成绩校验程序」，截止 3 天后。', 'ASSIGNMENT', 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 1, '批改提醒：作业「异常处理实战」已有 5 份提交待批改。', 'TEACHER', 0, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- ===== 其余学生的每日活跃（教师看板趋势用，确定性伪随机） =====
INSERT INTO `daily_activity` (user_id, stat_date, minutes)
SELECT u.id, DATE_SUB(CURDATE(), INTERVAL d.d DAY),
       30 + ABS(MOD(u.id * 31 + d.d * 17, 150))
FROM `user` u
JOIN (SELECT 0 AS d UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) d
WHERE u.role = 'STUDENT' AND u.id > 2;
