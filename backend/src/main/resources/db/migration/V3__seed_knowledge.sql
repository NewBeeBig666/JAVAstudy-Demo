-- V3: 知识点树种子（来源：本地资料库《智能Java编程-课程知识点.xlsx》+《教学大纲》11单元）
-- 一级知识点 id 1-5（即前端 modules m1-m5），二级知识点 id 101-127（即前端 knowledgePoints）
-- unit_no 对应教学大纲单元

INSERT INTO `knowledge_point` (id, kp_code, name, parent_id, level, unit_no, description, resources, sort_order) VALUES
-- ============ 一级：编程基础 ============
(1, 'm1', '编程基础', NULL, 1, NULL, 'Java 语言概述、基本语法、结构化程序设计：从环境搭建到方法与数组。', '["教材第1-3章"]', 1),
-- ============ 一级：类与对象基础 ============
(2, 'm2', '类与对象基础', NULL, 1, NULL, '面向对象思想、类与对象、封装、构造方法与 static 关键字。', '["教材第4章"]', 2),
-- ============ 一级：面向对象高阶 ============
(3, 'm3', '面向对象高阶', NULL, 1, NULL, '继承、多态、抽象类、接口、内部类与异常处理。', '["教材第5-6章"]', 3),
-- ============ 一级：集合与Java核心API ============
(4, 'm4', '集合与Java核心API', NULL, 1, NULL, 'Java 常用类、Lambda、泛型、集合框架与 Stream API。', '["教材第7-9章"]', 4),
-- ============ 一级：持久化 ============
(5, 'm5', '持久化', NULL, 1, NULL, 'I/O 流与 JDBC 数据库编程。', '["教材第10-11章"]', 5);

INSERT INTO `knowledge_point` (id, kp_code, name, parent_id, level, unit_no, description, resources, sort_order) VALUES
-- ============ m1 编程基础（9 个二级） ============
(101, 'kp-intro',    'Java 概述',      1, 2, 1, '发展历程、语言特点（跨平台/安全/多线程）、程序运行机制（编译+JVM）、JDK 安装配置与 IDEA 使用。【重点】Java特点、编写程序；【难点】运行机制。', '["教材第1章", "课件01-Java概述.pdf"]', 1),
(102, 'kp-syntax',   'Java 基本语法',  1, 2, 2, '标识符命名规则、关键字、三类注释、程序基本结构（包/导入/类/入口方法）。', '["教材第2章", "课件02-语法基础.pdf"]', 2),
(103, 'kp-var',      '变量与数据类型', 1, 2, 2, '八种基本数据类型及范围、引用类型、变量定义与作用域、自动/强制类型转换。【难点】变量作用域。', '["教材第2章"]', 3),
(104, 'kp-operator', '运算符与表达式', 1, 2, 2, '算术/赋值/关系/逻辑/位/三元运算符、优先级、表达式。【难点】运算符优先级。', '["教材第2章"]', 4),
(105, 'kp-select',   '选择结构',       1, 2, 3, 'if、if-else、if-else if-else 多分支与 switch 语句语法与适用场景。', '["教材第3章"]', 5),
(106, 'kp-loop',     '循环结构',       1, 2, 3, 'for、while、do-while 与嵌套循环。【难点】嵌套循环。', '["教材第3章"]', 6),
(107, 'kp-jump',     '跳转语句',       1, 2, 3, 'break 跳出循环、continue 终止本次进入下一次。', '["教材第3章"]', 7),
(108, 'kp-array',    '数组',           1, 2, 3, '一维/二维数组声明与静态、动态初始化、元素访问、内存存储方式、索引越界与空指针异常。【重点】一维数组。', '["教材第3章"]', 8),
(109, 'kp-method',   '方法',           1, 2, 3, '方法定义与调用、参数与返回值、方法重载（参数列表不同）。【难点】方法重载。', '["教材第3章"]', 9),
-- ============ m2 类与对象基础（5 个二级） ============
(110, 'kp-oop-thought', '面向对象思想', 2, 2, 4, '面向过程 vs 面向对象思维、三大特性概述（封装/继承/多态）、对象与类的关系。【难点】面向对象思想本身。', '["教材第4章"]', 10),
(111, 'kp-class',      '类与对象',     2, 2, 4, '类定义（成员变量/成员方法）、对象创建与使用、对象引用传递。【重点】类的定义、对象创建与使用。', '["教材第4章"]', 11),
(112, 'kp-encap',      '封装性',       2, 2, 4, '封装概念（隐藏细节+公共接口）、四种访问修饰符、成员变量私有化与 getter/setter。【重点】封装实现。', '["教材第4章"]', 12),
(113, 'kp-ctor',       '构造方法与this', 2, 2, 4, '构造方法定义与作用、构造方法重载、this 关键字（访问成员/调用构造/返回对象）。【重点】构造方法。', '["教材第4章"]', 13),
(114, 'kp-static',     'static 关键字', 2, 2, 4, '静态变量、静态方法、静态代码块与执行时机、static 的访问规则。【重点】静态变量与静态方法。', '["教材第4章"]', 14),
-- ============ m3 面向对象高阶（6 个二级） ============
(115, 'kp-inherit',   '继承',     3, 2, 5, '继承概念与语法、父类子类关系、方法重写规则、super 与 final 关键字。【重点】继承。', '["教材第5章"]', 15),
(116, 'kp-abstract',  '抽象类',   3, 2, 5, '抽象方法与抽象类定义、应用场景；抽象类不能实例化、子类必须实现抽象方法。【重点】抽象类。', '["教材第5章"]', 16),
(117, 'kp-interface', '接口',     3, 2, 5, '接口定义与实现、默认方法与静态方法、面向接口编程的应用场景。【重点】接口。', '["教材第5章"]', 17),
(118, 'kp-poly',      '多态',     3, 2, 5, '向上/向下转型、instanceof、多态的概念与实现（编译看左边运行看右边）。【重点&难点】多态。', '["教材第5章"]', 18),
(119, 'kp-exception', '异常处理', 3, 2, 6, '异常分类与继承体系、try-catch-finally、try-with-resources、throw/throws、自定义异常。【重点】try-catch-finally；【难点】自定义异常。', '["教材第6章", "课件06-异常处理.pdf"]', 19),
(120, 'kp-inner',     '内部类',   3, 2, 7, '成员内部类、局部内部类、匿名内部类（重点）、静态内部类的定义与访问规则、应用场景。', '["教材第7章"]', 20),
-- ============ m4 集合与Java核心API（5 个二级） ============
(121, 'kp-api',        'Java 常用API', 4, 2, 8, 'String/StringBuilder、Math、Random、日期时间类（LocalDate/DateTimeFormatter）、包装类与自动装箱拆箱。', '["教材第8章"]', 21),
(122, 'kp-lambda',     'Lambda 表达式', 4, 2, 7, 'Lambda 语法、函数式接口、方法引用、最佳实践。【重点&难点】Lambda。', '["教材第7章"]', 22),
(123, 'kp-generic',    '泛型',          4, 2, 7, '泛型类/接口/方法、类型擦除、通配符上下界（extends/super）、泛型在集合中的应用。', '["教材第7章"]', 23),
(124, 'kp-collection', '集合框架',      4, 2, 9, '集合框架概述、List（ArrayList/LinkedList）、Set（HashSet/TreeSet）、Map（HashMap）及遍历。【重点】List/Set/Map；【难点】Map 及其实现类。', '["教材第9章", "课件09-集合框架.pdf"]', 24),
(125, 'kp-stream',     'Stream API',    4, 2, 7, '流的创建、中间操作（filter/map/sorted）、终止操作（collect/reduce）、与 Lambda 配合的数据处理。', '["教材第7章"]', 25),
-- ============ m5 持久化（2 个二级） ============
(126, 'kp-io',   'I/O 流', 5, 2, 10, 'File 类、字节流（InputStream/OutputStream）、字符流（Reader/Writer）、缓冲流、对象流与序列化。【重点】字节流/字符流；【难点】I/O 流体系。', '["教材第10章"]', 26),
(127, 'kp-jdbc', 'JDBC',   5, 2, 11, 'MySQL 基础与 SQL、JDBC 编程步骤、Connection/Statement/PreparedStatement/ResultSet、数据库连接池。【重点】JDBC API；【难点】实现 JDBC 程序。', '["教材第11章", "课件15-JDBC.pdf"]', 27);

-- ============ 知识点依赖（PRE：kp_id 依赖 dep_kp_id） ============
-- L2 内部依赖
INSERT INTO `kp_dependency` (kp_id, dep_kp_id, dep_type) VALUES
(102, 101, 'PRE'),
(103, 102, 'PRE'),
(104, 103, 'PRE'),
(105, 102, 'PRE'),
(106, 105, 'PRE'),
(107, 106, 'PRE'),
(108, 106, 'PRE'),
(109, 102, 'PRE'),
(110, 109, 'PRE'),
(110, 108, 'PRE'),
(111, 110, 'PRE'),
(112, 111, 'PRE'),
(113, 112, 'PRE'),
(114, 111, 'PRE'),
(115, 112, 'PRE'),
(115, 113, 'PRE'),
(116, 115, 'PRE'),
(117, 115, 'PRE'),
(118, 115, 'PRE'),
(118, 116, 'PRE'),
(118, 117, 'PRE'),
(119, 111, 'PRE'),
(119, 109, 'PRE'),
(120, 111, 'PRE'),
(121, 111, 'PRE'),
(122, 117, 'PRE'),
(123, 111, 'PRE'),
(124, 123, 'PRE'),
(124, 117, 'PRE'),
(124, 115, 'PRE'),
(125, 122, 'PRE'),
(125, 124, 'PRE'),
(126, 119, 'PRE'),
(126, 121, 'PRE'),
(127, 119, 'PRE'),
(127, 124, 'PRE');

-- ============ 学生掌握度种子 ============
-- 所有学生：确定性伪随机 25-94 分
INSERT INTO `mastery` (user_id, kp_id, mastery_value)
SELECT u.id, k.id, 25 + ABS(MOD(u.id * 7 + k.id * 13, 70))
FROM `user` u JOIN `knowledge_point` k ON k.level = 2
WHERE u.role = 'STUDENT';

-- 张明（student）覆盖为符合画像的值：集合/泛型/多态偏弱
UPDATE `mastery` SET mastery_value = 88 WHERE user_id = 2 AND kp_id = 101;
UPDATE `mastery` SET mastery_value = 82 WHERE user_id = 2 AND kp_id = 102;
UPDATE `mastery` SET mastery_value = 78 WHERE user_id = 2 AND kp_id = 103;
UPDATE `mastery` SET mastery_value = 74 WHERE user_id = 2 AND kp_id = 104;
UPDATE `mastery` SET mastery_value = 80 WHERE user_id = 2 AND kp_id = 105;
UPDATE `mastery` SET mastery_value = 72 WHERE user_id = 2 AND kp_id = 106;
UPDATE `mastery` SET mastery_value = 70 WHERE user_id = 2 AND kp_id = 107;
UPDATE `mastery` SET mastery_value = 62 WHERE user_id = 2 AND kp_id = 108;
UPDATE `mastery` SET mastery_value = 68 WHERE user_id = 2 AND kp_id = 109;
UPDATE `mastery` SET mastery_value = 74 WHERE user_id = 2 AND kp_id = 110;
UPDATE `mastery` SET mastery_value = 70 WHERE user_id = 2 AND kp_id = 111;
UPDATE `mastery` SET mastery_value = 66 WHERE user_id = 2 AND kp_id = 112;
UPDATE `mastery` SET mastery_value = 63 WHERE user_id = 2 AND kp_id = 113;
UPDATE `mastery` SET mastery_value = 60 WHERE user_id = 2 AND kp_id = 114;
UPDATE `mastery` SET mastery_value = 58 WHERE user_id = 2 AND kp_id = 115;
UPDATE `mastery` SET mastery_value = 48 WHERE user_id = 2 AND kp_id = 116;
UPDATE `mastery` SET mastery_value = 45 WHERE user_id = 2 AND kp_id = 117;
UPDATE `mastery` SET mastery_value = 50 WHERE user_id = 2 AND kp_id = 118;
UPDATE `mastery` SET mastery_value = 66 WHERE user_id = 2 AND kp_id = 119;
UPDATE `mastery` SET mastery_value = 44 WHERE user_id = 2 AND kp_id = 120;
UPDATE `mastery` SET mastery_value = 65 WHERE user_id = 2 AND kp_id = 121;
UPDATE `mastery` SET mastery_value = 46 WHERE user_id = 2 AND kp_id = 122;
UPDATE `mastery` SET mastery_value = 35 WHERE user_id = 2 AND kp_id = 123;
UPDATE `mastery` SET mastery_value = 42 WHERE user_id = 2 AND kp_id = 124;
UPDATE `mastery` SET mastery_value = 38 WHERE user_id = 2 AND kp_id = 125;
UPDATE `mastery` SET mastery_value = 55 WHERE user_id = 2 AND kp_id = 126;
UPDATE `mastery` SET mastery_value = 60 WHERE user_id = 2 AND kp_id = 127;

-- 张明近 7 日学习时长（分钟）
INSERT INTO `daily_activity` (user_id, stat_date, minutes) VALUES
(2, DATE_SUB(CURDATE(), INTERVAL 6 DAY), 126),
(2, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 90),
(2, DATE_SUB(CURDATE(), INTERVAL 4 DAY), 192),
(2, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 48),
(2, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 156),
(2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 114),
(2, CURDATE(), 144);
