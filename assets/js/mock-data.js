/* ===========================================================
 * 集中管理的 Mock 数据（纯前端 Demo，无后端 / 无鉴权）
 * 所有界面状态（加载 / 空数据 / 已完成）均由这份数据 + 运行时副本驱动
 * =========================================================== */
window.MOCK = {

  /* ---------- 多智能体定义（与产品设计文档一致） ---------- */
  agents: {
    diagnosis: { name: '诊断Agent',  color: '#7c3aed', desc: '入学诊断、知识图谱掌握度推断、薄弱点溯源' },
    planning:  { name: '规划Agent',  color: '#4f46e5', desc: '依赖链解析、个性化学习路径拆解与排期' },
    tutor:     { name: '辅导Agent',  color: '#0f7fd4', desc: '渐进式讲解、RAG 检索、场景化答疑' },
    code:      { name: '代码陪练Agent', color: '#c77700', desc: '测试用例驱动，三级渐进提示，不直接给完整答案' },
    grading:   { name: '批改Agent',  color: '#12a150', desc: 'Rubric 驱动评分、AI 初评、代码相似度检测' },
    insight:   { name: '学情Agent',  color: '#7c3aed', desc: '班级学情聚合、卡点分析、预警名单生成' }
  },

  /* ---------- 知识点图谱（Java 后端方向） ---------- */
  modules: [
    { id: 'm1', name: '语言基础' },
    { id: 'm2', name: '面向对象' },
    { id: 'm3', name: '核心类库' },
    { id: 'm4', name: '并发与 JVM' },
    { id: 'm5', name: '工程与框架' }
  ],
  knowledgePoints: [
    { id: 'basic',      name: 'Java 基础语法',      module: 'm1', mastery: 88, deps: [],
      desc: '变量与类型、运算符、流程控制、数组、字符串与常用 API。',
      resources: ['《Java 核心技术》Ch.3', '课件 02-语法基础.pdf'] },
    { id: 'exception',  name: '异常处理',           module: 'm1', mastery: 66, deps: ['basic'],
      desc: '受检/非受检异常、try-with-resources、自定义异常与异常链。',
      resources: ['课件 06-异常处理.pdf'] },
    { id: 'algo',       name: '数据结构与算法',     module: 'm1', mastery: 52, deps: ['basic'],
      desc: '数组/链表/栈/队列/哈希表、排序与二分、复杂度估算。',
      resources: ['LeetCode 专题：数组与哈希表'] },
    { id: 'oop',        name: '面向对象与设计原则', module: 'm2', mastery: 74, deps: ['basic'],
      desc: '封装继承多态、抽象类与接口、SOLID 原则、常用设计模式。',
      resources: ['《Head First 设计模式》Ch.1-4'] },
    { id: 'collection', name: '集合框架',           module: 'm3', mastery: 42, deps: ['basic', 'generic'],
      desc: 'List/Set/Map 家族、HashMap 底层与扩容、ConcurrentHashMap、fail-fast。',
      resources: ['源码阅读：HashMap#resize', '课件 09-集合框架.pdf'] },
    { id: 'generic',    name: '泛型与反射',         module: 'm3', mastery: 35, deps: ['oop'],
      desc: '泛型擦除、通配符上下界、Class/Field/Method 反射、注解处理。',
      resources: ['课件 10-泛型.pdf'] },
    { id: 'io',         name: 'IO 与 NIO',          module: 'm3', mastery: 55, deps: ['basic', 'exception'],
      desc: '字节/字符流、序列化、Buffer/Channel/Selector、零拷贝。',
      resources: ['课件 12-NIO.pdf'] },
    { id: 'concurrent', name: '并发编程',           module: 'm4', mastery: 27, deps: ['jvm', 'oop'],
      desc: '线程生命周期、synchronized/volatile、JUC 工具类、线程池参数与拒绝策略。',
      resources: ['《Java 并发编程实战》Ch.2-8', '课件 14-并发.pdf'] },
    { id: 'jvm',        name: 'JVM 与内存模型',     module: 'm4', mastery: 31, deps: ['basic', 'oop'],
      desc: '运行时数据区、类加载机制、GC 算法与收集器、JMM/happens-before。',
      resources: ['《深入理解 JVM》Ch.2-3', '课件 13-JVM.pdf'] },
    { id: 'jdbc',       name: 'JDBC 与数据库访问',  module: 'm5', mastery: 60, deps: ['basic', 'exception'],
      desc: 'DriverManager/连接池、PreparedStatement、事务隔离级别、SQL 注入防护。',
      resources: ['课件 15-JDBC.pdf'] },
    { id: 'spring',     name: 'Spring Boot 基础',   module: 'm5', mastery: 44, deps: ['oop', 'jdbc'],
      desc: 'IoC/DI、Bean 生命周期、AOP、自动配置 Starter、REST 接口开发。',
      resources: ['Spring 官方文档 Core', '课件 16-SpringBoot.pdf'] },
    { id: 'test',       name: '单元测试与调试',     module: 'm5', mastery: 71, deps: ['oop'],
      desc: 'JUnit5 断言与生命周期、Mockito 打桩、覆盖率、断点调试技巧。',
      resources: ['课件 17-单元测试.pdf'] }
  ],

  /* ---------- 学生画像 ---------- */
  student: {
    id: 's01', name: '张明', sid: '2023211024',
    className: '软件工程 2023 级 2 班',
    streakDays: 12, weekHours: 6.5, doneExercises: 87, avgMastery: 58,
    weekActivity: [2.1, 1.5, 3.2, 0.8, 2.6, 1.9, 2.4],
    masteryTrend: [49, 51, 53, 52, 55, 57, 58],
    reviewQueue: [
      { kp: 'collection', title: 'HashMap 扩容时元素迁移规则', due: '今天', reason: 'SM-2 第 3 次复习' },
      { kp: 'concurrent', title: 'volatile 的可见性与禁止重排序', due: '今天', reason: '昨日练习错误' },
      { kp: 'generic',    title: '通配符 extends / super 使用场景', due: '明天', reason: '掌握度低于 40' }
    ],
    wrongBook: [
      { id: 'w1', kp: 'collection', title: 'HashMap resize 元素迁移判定', wrongCount: 2, lastAt: '09-26' },
      { id: 'w2', kp: 'concurrent', title: 'synchronized 与 Lock 的取舍', wrongCount: 3, lastAt: '09-27' },
      { id: 'w3', kp: 'jvm',        title: 'GC Roots 包含哪些对象', wrongCount: 1, lastAt: '09-24' },
      { id: 'w4', kp: 'generic',    title: '泛型擦除后的桥接方法', wrongCount: 2, lastAt: '09-22' },
      { id: 'w5', kp: 'spring',     title: '@Transactional 自调用失效', wrongCount: 1, lastAt: '09-20' }
    ]
  },

  /* ---------- 教师与班级 ---------- */
  teacher: { id: 't01', name: '陈老师', title: '副教授', course: 'Java 后端开发（2026 秋）' },
  classes: [
    { id: 'c1', name: '软件工程 2023 级 2 班', size: 32, avgMastery: 56, submitRate: 78, riskCount: 4 },
    { id: 'c2', name: '软件工程 2023 级 1 班', size: 30, avgMastery: 61, submitRate: 85, riskCount: 2 }
  ],
  students: [
    { id: 's01', name: '张明',   mastery: 58, submitRate: 92, absDays: 0, risk: 'low',  weak: ['concurrent', 'jvm'],       trend: [2,3,1,4,2,3,2], attempts: 18 },
    { id: 's02', name: '李思远', mastery: 72, submitRate: 100, absDays: 0, risk: 'low', weak: ['generic'],                 trend: [3,3,4,3,4,3,4], attempts: 24 },
    { id: 's03', name: '王雨欣', mastery: 45, submitRate: 67,  absDays: 3, risk: 'high', weak: ['concurrent','collection'], trend: [3,2,2,1,1,0,0], attempts: 9 },
    { id: 's04', name: '陈子豪', mastery: 63, submitRate: 83,  absDays: 1, risk: 'low', weak: ['jvm'],                      trend: [2,2,3,3,2,3,3], attempts: 16 },
    { id: 's05', name: '刘佳怡', mastery: 51, submitRate: 75,  absDays: 0, risk: 'mid', weak: ['collection','generic'],     trend: [2,2,1,2,2,2,2], attempts: 13 },
    { id: 's06', name: '赵启明', mastery: 38, submitRate: 42,  absDays: 5, risk: 'high', weak: ['concurrent','jvm','collection'], trend: [4,3,2,1,0,0,0], attempts: 6 },
    { id: 's07', name: '孙悦',   mastery: 69, submitRate: 92,  absDays: 0, risk: 'low', weak: ['spring'],                   trend: [3,3,3,4,3,3,3], attempts: 21 },
    { id: 's08', name: '周凯',   mastery: 55, submitRate: 58,  absDays: 2, risk: 'mid', weak: ['concurrent','algo'],        trend: [2,3,1,2,1,1,1], attempts: 11 },
    { id: 's09', name: '吴静',   mastery: 74, submitRate: 100, absDays: 0, risk: 'low', weak: [],                           trend: [4,4,3,4,4,4,4], attempts: 26 },
    { id: 's10', name: '郑浩',   mastery: 41, submitRate: 50,  absDays: 4, risk: 'high', weak: ['concurrent','jvm'],        trend: [3,2,2,1,1,0,1], attempts: 8 },
    { id: 's11', name: '冯晓',   mastery: 60, submitRate: 88,  absDays: 1, risk: 'low', weak: ['io'],                       trend: [2,3,3,2,3,3,2], attempts: 17 },
    { id: 's12', name: '蒋楠',   mastery: 49, submitRate: 71,  absDays: 2, risk: 'mid', weak: ['collection','spring'],      trend: [2,2,2,3,2,2,1], attempts: 14 }
  ],

  /* ---------- 题库（含三级渐进提示，按产品设计文档「不直接给完整代码」约束） ---------- */
  exercises: [
    {
      id: 'ex-concurrent-1', kp: 'concurrent', type: 'choice', difficulty: '中等',
      stem: '需要实现一个高并发下的请求计数器，要求低开销且线程安全。下列方案中最合适的是？',
      options: [
        '使用 volatile int count，在业务代码中 count++',
        '使用 AtomicInteger 并调用 incrementAndGet()',
        '使用 HashMap 存储，并用 synchronized 修饰整个方法',
        '使用 ArrayList 配合 Collections.synchronizedList()'
      ],
      answer: 1, masteryDelta: 8,
      analysis: 'volatile 只能保证可见性，不能保证复合操作 count++ 的原子性（读-改-写三步）；synchronized 修饰整个方法在高并发下退化为串行，开销大；AtomicInteger 基于 CAS + volatile，是无锁方案里最合适的选择。注意：极高竞争下 LongAdder 的分段累加性能更好，这是下一节的延伸。',
      hints: [
        '先想清楚：count++ 实际包含「读取 → 加一 → 写回」三步，volatile 能覆盖其中哪一步？',
        '再比较：方案三把整个方法串行化了，QPS 会掉到什么量级？它适合什么场景？',
        '最后确认：AtomicInteger 底层是 Unsafe.compareAndSwapInt + volatile value，属于乐观锁；脑子中画出 CAS 失败重试的循环。'
      ]
    },
    {
      id: 'ex-collection-1', kp: 'collection', type: 'choice', difficulty: '中等',
      stem: 'JDK 8 的 HashMap 在 resize() 时，如何决定某个元素是留在原桶位还是迁移到高位桶？',
      options: [
        '用新容量重新计算 hash % newCap 得出下标',
        '判断 e.hash & oldCap 的结果，为 0 则留在原位，否则迁移到 原下标 + oldCap',
        '把所有 key 取出重新 put 一遍，由 put 自动分配',
        '随机分配到新旧两个桶中以保持均匀'
      ],
      answer: 1, masteryDelta: 8,
      analysis: '因为容量恒为 2 的幂，e.hash & oldCap 只可能得到 0 或 oldCap 两个值：为 0 说明新增的高位 bit 为 0，元素下标不变；否则下标 = 原下标 + oldCap。这避免了对每个元素重新取模，是 JDK 8 相比 JDK 7 的重要优化。',
      hints: [
        '抓住一个前提：HashMap 的容量为什么始终保持 2 的幂？',
        'oldCap 的二进制只有一个 1，那么 hash & oldCap 的结果有几种可能？',
        '把「原下标 + oldCap」代入一个具体例子（oldCap=16，hash=21）验算一遍。'
      ]
    },
    {
      id: 'ex-generic-1', kp: 'generic', type: 'choice', difficulty: '困难',
      stem: '关于 Java 泛型的类型擦除，下列说法正确的是？',
      options: [
        '运行时可以通过 list.getClass() 拿到 List<String> 的完整泛型信息',
        '无界泛型 T 在字节码中被擦除为 Object，有上界 <? extends Number> 被擦除为 Number',
        '泛型信息会写入 class 文件的运行时常量池，供 JVM 在运行时校验',
        '反射可以在运行时修改一个 List<Integer> 的泛型参数使其变为 List<String>'
      ],
      answer: 1, masteryDelta: 9,
      analysis: '泛型是编译期语法糖：无界 T 擦除为 Object，有上界擦除为上界类型；擦除后编译器会自动插入强制转型与桥接方法。签名信息（Signature 属性）会保留在 class 文件中，但仅用于反射 API 读取，不参与运行时类型校验，因此「泛型擦除后无法在运行时区分 List<String> 与 List<Integer>」。',
      hints: [
        '先想：new ArrayList<String>() 与 new ArrayList<Integer>() 的 getClass() 返回什么？',
        '再想：既然擦除了，为什么调用 get() 时不需要手写强转？谁帮你插入的？',
        '区分两个概念：「字节码中的 Signature 属性（反射可读）」与「运行时的实际类型约束」。'
      ]
    },
    {
      id: 'ex-jvm-1', kp: 'jvm', type: 'choice', difficulty: '中等',
      stem: '下列哪一种情况最不可能导致 Full GC 频繁触发？',
      options: [
        '老年代空间担保失败（promotion failed）',
        'System.gc() 被第三方框架定期调用且未加 -XX:+DisableExplicitGC',
        'Metaspace 达到 MaxMetaspaceSize 并持续增长',
        '年轻代 Eden 区对象朝生夕死，Minor GC 后大部分被回收'
      ],
      answer: 3, masteryDelta: 7,
      analysis: '「朝生夕死」正是分代收集假设所描述的理想情况，Minor GC 高效且不会触发 Full GC。前三项都会直接导致老年代或元空间压力，从而引发 Full GC。排查顺序建议：先看 GC 日志与堆转储，再定位是内存泄漏还是容量配置不足。',
      hints: [
        '回忆分代收集假说：大部分对象朝生夕死 —— 这是「健康」还是「异常」？',
        'promotion failed 意味着什么？老年代放不下从年轻代晋升的对象时会怎样？',
        'Metaspace 存的是什么？动态生成类（CGLib、Groovy）会让它增长吗？'
      ]
    },
    {
      id: 'ex-exception-1', kp: 'exception', type: 'code', difficulty: '中等',
      stem: '下面这段代码的返回值是什么？请说明原因。',
      code: 'public static int test() {\n    int i = 1;\n    try {\n        return ++i;\n    } finally {\n        return i++;\n    }\n}',
      options: ['返回 1', '返回 2', '返回 3', '编译错误'],
      answer: 1, masteryDelta: 6,
      analysis: 'try 中执行 ++i 后 i=2，return 的值 2 会被暂存；但 finally 中的 return 会覆盖try 的返回出口，此时执行 i++ 表达式的值为 2（后置自增先返回旧值），所以最终返回 2，i 变成 3 但已无影响。结论：永远不要在 finally 中写 return，它会吞掉异常并覆盖正常返回值。',
      hints: [
        '先分清 ++i（先增后取值）与 i++（先取值后增）的差别。',
        '再想 finally 的执行时机：是在 return「保存返回值之后、方法真正返回之前」。',
        '关键点：finally 里的 return 会直接替换掉 try 中已经暂存好的返回出口。'
      ]
    },
    {
      id: 'ex-spring-1', kp: 'spring', type: 'choice', difficulty: '中等',
      stem: '下列哪一项不会导致 Spring 的 @Transactional 事务失效？',
      options: [
        '在同一个类中用普通方法直接调用被 @Transactional 标注的方法',
        '把 @Transactional 标注在 private 方法上',
        '数据库引擎使用 InnoDB',
        '异常被 try-catch 吞掉且未重新抛出'
      ],
      answer: 2, masteryDelta: 7,
      analysis: '@Transactional 基于 AOP 代理：自调用绕过了代理对象、private 方法无法被代理、异常被吞掉会导致提交而非回滚。而 InnoDB 是支持事务的引擎（MyISAM 才不支持），所以它不会导致事务失效。',
      hints: [
        '先明确 @Transactional 的实现机制：是编译期织入还是运行时代理？',
        '自调用时 this 指向的是原对象还是代理对象？',
        '默认回滚规则是遇到什么类型的异常？被 catch 掉之后框架还能感知吗？'
      ]
    },
    {
      id: 'ex-jdbc-1', kp: 'jdbc', type: 'choice', difficulty: '简单',
      stem: '关于使用 PreparedStatement 的说法，正确的是？',
      options: [
        'PreparedStatement 只能防 SQL 注入，对性能没有帮助',
        'PreparedStatement 会预编译 SQL 模板，既能防注入，也能在批量执行时减少解析开销',
        '只要用了 PreparedStatement，就一定能用上数据库的查询缓存',
        'PreparedStatement 的参数占位符可以直接拼接表名和 ORDER BY 字段'
      ],
      answer: 1, masteryDelta: 5,
      analysis: 'PreparedStatement 先把 SQL 模板发给数据库预编译，参数以绑定变量方式传入，不再参与 SQL 语义解析，因此天然防注入。批量执行时复用同一模板可显著减少硬解析。但占位符只能绑定「值」，不能绑定表名、列名、排序方向等标识符，这些仍需在服务端做白名单校验。',
      hints: [
        '注入的本质是什么？参数在什么情况下会被当成 SQL 语义的一部分？',
        '预编译发生在哪一步？批量插入时模板被解析几次？',
        '思考反例：SELECT * FROM ? 能执行吗？为什么？'
      ]
    },
    {
      id: 'ex-oop-1', kp: 'oop', type: 'choice', difficulty: '简单',
      stem: '「面向接口编程而非实现编程」最直接对应的设计原则是？',
      options: ['单一职责原则 SRP', '依赖倒置原则 DIP', '里氏替换原则 LSP', '接口隔离原则 ISP'],
      answer: 1, masteryDelta: 5,
      analysis: '依赖倒置原则要求高层模块不依赖低层模块，二者都依赖抽象；抽象不依赖细节，细节依赖抽象。这正是「面向接口编程」的理论依据。ISP 强调的是接口本身要小而专，不要把不相关的方法塞进同一个接口。',
      hints: [
        '题干关键词是「依赖谁」—— 依赖的对象是具体类还是抽象？',
        'DIP 的两句话背一下：高层不依赖低层，二者都依赖抽象；抽象不依赖细节，细节依赖抽象。',
        '区分 ISP：「接口要小」解决的是实现类被迫实现无用方法的问题。'
      ]
    }
  ],

  /* ---------- 预置学习会话 ---------- */
  sessions: [
    {
      id: 'se-1',
      title: '并发编程：从 volatile 到线程池',
      kp: 'concurrent',
      status: 'active',          // active | done
      createdAt: '09-27 20:14',
      updatedAt: '09-28 09:32',
      stepIndex: 2,              // 当前进行到第 3 步
      steps: [
        { title: '诊断定位：并发摸底 5 题', desc: '快速判断你卡在「内存模型」还是「JUC 工具使用」上。', dur: '5 分钟', status: 'done' },
        { title: '概念精讲：JMM 与 happens-before', desc: '从可见性、原子性、有序性三条线索讲清 volatile 的边界。', dur: '15 分钟', status: 'done' },
        { title: '代码陪练：无锁计数器', desc: '给出测试用例，分三级提示引导你补全 AtomicInteger / LongAdder 实现。', dur: '25 分钟', status: 'active' },
        { title: '巩固测验 + 错题归档', desc: '2 道变式题即时评测，错题自动进入间隔重复队列。', dur: '10 分钟', status: 'pending' }
      ],
      messages: [
        { id: 'm1', from: 'sys', text: '会话已创建 · 已载入你的知识图谱快照（12 个知识点）' },
        { id: 'm2', from: 'user', text: '并发这块我一直学不明白，synchronized 和 volatile 总是混，想系统地补一下。' },
        {
          id: 'm3', from: 'agent', agent: 'diagnosis', time: '20:14',
          text: '我先调了你的历史练习记录和错题本，做了次快速诊断：\n\n你的并发编程掌握度只有 **27 分**，但更关键的是——**根源不在并发本身**。顺着依赖链往上追：并发编程 ← JVM 与内存模型（31 分）← Java 基础语法（88 分）。\n\n也就是说，你真正缺的是「内存可见性与指令重排序」这块地基，JUC 工具类只是上层表现。直接刷线程池的题目，收益很低。',
          card: { type: 'diagnosis', items: [
            { kp: 'concurrent', mastery: 27, note: '目标知识点，直接得分最低' },
            { kp: 'jvm',        mastery: 31, note: '前置薄弱 · 判定为根本原因' },
            { kp: 'oop',        mastery: 74, note: '前置达标' }
          ], conclusion: '建议优先补 JVM 内存模型，再回到并发。' }
        },
        {
          id: 'm4', from: 'agent', agent: 'planning', time: '20:15',
          text: '按诊断结果，我给你排了一条 55 分钟的路径，前两步已经带你走过一遍了，现在停在第 3 步。每一步都可以点开看详情，右侧上下文区会同步当前知识点摘要。',
          card: { type: 'path', kp: 'concurrent' }
        },
        { id: 'm5', from: 'user', text: '好，那我们开始第 3 步的练习吧。', time: '09:31' },
        {
          id: 'm6', from: 'agent', agent: 'code', time: '09:32',
          text: '这道是代码陪练题。按规则我**不会直接给你完整实现**，先读测试用例，自己补全核心逻辑；卡住了再逐级要提示（提示使用次数教师端可见）。',
          card: { type: 'exercise', exId: 'ex-concurrent-1', state: 'answering', hintLevel: 0, selected: null }
        }
      ]
    },
    {
      id: 'se-2',
      title: '集合框架：HashMap 底层原理',
      kp: 'collection',
      status: 'done',
      createdAt: '09-24 19:02',
      updatedAt: '09-25 20:41',
      stepIndex: 3,
      steps: [
        { title: '诊断定位：集合类选型摸底', desc: '区分 List / Set / Map 使用与底层实现两类问题。', dur: '5 分钟', status: 'done' },
        { title: '概念精讲：哈希冲突与树化', desc: '拉链法、负载因子、树化阈值 8 与退化阈值 6。', dur: '15 分钟', status: 'done' },
        { title: '代码陪练：手写简易 HashMap', desc: '补全 put / resize / 迁移三段核心逻辑。', dur: '25 分钟', status: 'done' },
        { title: '巩固测验 + 错题归档', desc: '变式题评测，错题入复习队列。', dur: '10 分钟', status: 'done' }
      ],
      messages: [
        { id: 'n1', from: 'sys', text: '会话已创建 · 目标知识点：集合框架' },
        { id: 'n2', from: 'user', text: 'HashMap 扩容的时候元素是怎么搬家的？看源码没看懂。' },
        {
          id: 'n3', from: 'agent', agent: 'tutor', time: '19:03',
          text: '关键就在这一行位运算上，我把它从 `java.util.HashMap#resize` 里摘出来了：',
          card: { type: 'code', lang: 'java', title: 'HashMap#resize 元素迁移（JDK 8）',
            code: 'if ((e.hash & oldCap) == 0) {\n    // 高位 bit 为 0：下标不变，留在原桶\n    if (loTail == null) loHead = e;\n    else loTail.next = e;\n    loTail = e;\n} else {\n    // 高位 bit 为 1：迁移到 原下标 + oldCap\n    if (hiTail == null) hiHead = e;\n    else hiTail.next = e;\n    hiTail = e;\n}',
            explain: '因为容量恒为 2 的幂，oldCap 的二进制只有一个 1，所以 e.hash & oldCap 只有 0 / oldCap 两种结果，一次位运算就能决定去向，不需要重新取模。' }
        },
        {
          id: 'n4', from: 'agent', agent: 'code', time: '19:40',
          text: '来，用一道题验证一下你是否真的理解了迁移规则：',
          card: { type: 'exercise', exId: 'ex-collection-1', state: 'graded', hintLevel: 0, selected: 1 }
        },
        {
          id: 'n5', from: 'agent', agent: 'tutor', time: '19:42',
          text: '回答正确，解析如下：',
          card: { type: 'result', ok: true, exId: 'ex-collection-1', delta: 8 }
        },
        {
          id: 'n6', from: 'agent', agent: 'planning', time: '20:40',
          text: '四个步骤全部完成，这条路径收官了。你的「集合框架」掌握度从 34 提升到 **42**，已自动把「HashMap 扩容迁移规则」加入间隔重复队列，明天会再推一次变式题。',
          card: { type: 'report', title: '学习路径完成报告 · 集合框架',
            metrics: [ { label: '掌握度', value: '34 → 42' }, { label: '耗时', value: '52 分钟' }, { label: '练习正确率', value: '80%' }, { label: '提示使用', value: '1 次' } ],
            summary: '底层原理类问题已基本打通。下一步建议攻克「泛型与反射」（35 分），它是集合框架的前置依赖，会反过来影响你对 Collections 工具类源码的理解。' }
        },
        { id: 'n7', from: 'sys', text: '会话已完成 · 学习报告已同步至画像与报告' }
      ]
    }
  ],

  /* ---------- 作业（教师发布 → 学生可见） ---------- */
  assignments: [
    {
      id: 'as-1',
      title: '集合框架综合练习：手写 LRU 缓存',
      kp: ['collection', 'generic'],
      desc: '基于 LinkedHashMap 或「HashMap + 双向链表」实现一个支持容量上限的 LRU 缓存，要求 get/put 时间复杂度 O(1)，并补充边界与并发场景说明。',
      due: '2026-09-26 23:59',
      status: 'closed',            // draft | ongoing | closed
      classId: 'c1',
      total: 32, submitted: 27,
      rubric: [
        { name: '功能正确性', max: 40, desc: 'get/put 均 O(1)，淘汰顺序正确' },
        { name: '边界与异常处理', max: 20, desc: '容量为 0、key 为 null、重复 put' },
        { name: '复杂度与设计', max: 20, desc: '给出复杂度分析，结构清晰' },
        { name: '代码规范与注释', max: 20, desc: '命名、注释、无魔法数字' }
      ],
      createdAt: '09-22 10:12'
    },
    {
      id: 'as-2',
      title: '并发编程实战：线程池 + 无锁计数器',
      kp: ['concurrent', 'jvm'],
      desc: '（1）自定义 ThreadPoolExecutor，说明 corePoolSize / workQueue / 拒绝策略的取舍；（2）分别用 AtomicInteger 与 LongAdder 实现压测计数器，给出 100 万次累加的耗时对比与分析。',
      due: '2026-09-30 23:59',
      status: 'ongoing',
      classId: 'c1',
      total: 32, submitted: 14,
      rubric: [
        { name: '功能正确性', max: 40, desc: '线程池参数合理，计数器结果准确' },
        { name: '并发安全分析', max: 25, desc: '能说明 volatile 的边界与 CAS 的适用场景' },
        { name: '性能对比与结论', max: 20, desc: '有实测数据，结论可解释' },
        { name: '代码规范与注释', max: 15, desc: '命名、注释、压测脚本可复现' }
      ],
      createdAt: '09-27 14:30'
    },
    {
      id: 'as-3',
      title: 'Spring Boot 单元测试：Service 层 Mock 测试',
      kp: ['spring', 'test'],
      desc: '为给定 OrderService 编写 JUnit5 + Mockito 测试，覆盖正常下单、库存不足、支付超时三条分支，要求分支覆盖率 ≥ 80%。',
      due: '2026-10-08 23:59',
      status: 'draft',
      classId: 'c1',
      total: 32, submitted: 0,
      rubric: [
        { name: '用例完整性', max: 40, desc: '三条分支均有覆盖' },
        { name: 'Mock 使用合理性', max: 25, desc: '依赖隔离正确，无过度打桩' },
        { name: '断言质量', max: 20, desc: '断言具体且可读' },
        { name: '代码规范', max: 15, desc: '命名与组织结构' }
      ],
      createdAt: '09-28 09:00'
    }
  ],

  /* ---------- 学生提交 ---------- */
  submissions: [
    {
      id: 'sb-1', asId: 'as-1', sid: 's01', name: '张明', status: 'graded',
      submittedAt: '09-26 21:47', attempts: 3, similarity: 12, timeSlot: '正常时段',
      score: 82, aiScore: 80,
      code: 'public class LRUCache<K, V> extends LinkedHashMap<K, V> {\n    private final int capacity;\n\n    public LRUCache(int capacity) {\n        // accessOrder = true：按访问顺序排序，最近访问的放尾部\n        super(capacity, 0.75f, true);\n        this.capacity = capacity;\n    }\n\n    @Override\n    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {\n        return size() > capacity;\n    }\n}',
      aiComment: '继承 LinkedHashMap 的写法简洁且 get/put 均为 O(1)，思路正确。未处理 capacity <= 0 的非法入参；未说明该实现非线程安全。',
      teacherComment: '实现干净，思路正确。两点建议：① 构造时对 capacity 做参数校验并抛出 IllegalArgumentException；② 补充说明本实现非线程安全，多线程场景应改用 ConcurrentHashMap 或加锁，这一点正好是并发章节的内容。',
      rubricScores: { '功能正确性': 36, '边界与异常处理': 13, '复杂度与设计': 18, '代码规范与注释': 15 }
    },
    {
      id: 'sb-2', asId: 'as-1', sid: 's02', name: '李思远', status: 'graded',
      submittedAt: '09-25 19:20', attempts: 1, similarity: 9, timeSlot: '正常时段',
      score: 93, aiScore: 90,
      code: 'public class LRUCache<K, V> {\n    private final int capacity;\n    private final Map<K, Node> map;\n    private final Node head, tail;   // 哨兵节点，简化边界处理\n    ...\n}',
      aiComment: '手写 HashMap + 双向链表，哨兵节点处理得很干净，复杂度分析完整。',
      teacherComment: '手写实现完成度很高，哨兵节点是加分项。可进一步思考：链表操作在多线程下如何保证原子性。',
      rubricScores: { '功能正确性': 40, '边界与异常处理': 18, '复杂度与设计': 20, '代码规范与注释': 15 }
    },
    {
      id: 'sb-3', asId: 'as-2', sid: 's01', name: '张明', status: 'pending',
      submittedAt: '09-28 11:05', attempts: 4, similarity: 15, timeSlot: '正常时段',
      score: null, aiScore: 76,
      code: 'public class BenchCounter {\n    private final AtomicInteger ai = new AtomicInteger();\n    private final LongAdder la = new LongAdder();\n\n    void incAtomic() { ai.incrementAndGet(); }\n    void incAdder()  { la.increment(); }\n\n    public static void main(String[] args) throws Exception {\n        // 8 线程 × 125_000 次，用 CountDownLatch 保证同时起跑\n        ExecutorService pool = Executors.newFixedThreadPool(8);\n        CountDownLatch start = new CountDownLatch(1);\n        CountDownLatch done  = new CountDownLatch(8);\n        for (int i = 0; i < 8; i++) {\n            pool.submit(() -> {\n                start.await();\n                for (int j = 0; j < 125_000; j++) { /* TODO: 调用两种计数器 */ }\n                done.countDown();\n            });\n        }\n        long t0 = System.nanoTime();\n        start.countDown();\n        done.await();\n        System.out.printf("cost = %.2f ms%n", (System.nanoTime() - t0) / 1e6);\n        pool.shutdown();\n    }\n}',
      aiComment: '压测骨架（CountDownLatch 起跑 + FixedThreadPool）搭得规范。TODO 处尚未写入计数逻辑；缺少对 AtomicInteger 在高竞争下 CAS 自旋开销的分析结论。',
      teacherComment: '',
      rubricScores: { '功能正确性': 28, '并发安全分析': 18, '性能对比与结论': 14, '代码规范与注释': 11 }
    },
    {
      id: 'sb-4', asId: 'as-2', sid: 's02', name: '李思远', status: 'pending',
      submittedAt: '09-28 09:12', attempts: 2, similarity: 11, timeSlot: '正常时段',
      score: null, aiScore: 88,
      code: '// 线程池 + LongAdder 压测，附 JMH 基准测试脚本\n@Benchmark\npublic void atomic(BenchState s) { s.atomic.incrementAndGet(); }\n@Benchmark\npublic void adder(BenchState s)  { s.adder.increment(); }',
      aiComment: '使用 JMH 做基准测试，方法学优于手写 main 计时，值得在课上展示。',
      teacherComment: '',
      rubricScores: { '功能正确性': 36, '并发安全分析': 21, '性能对比与结论': 18, '代码规范与注释': 13 }
    },
    {
      id: 'sb-5', asId: 'as-2', sid: 's03', name: '王雨欣', status: 'pending',
      submittedAt: '09-28 23:41', attempts: 1, similarity: 34, timeSlot: '深夜时段',
      score: null, aiScore: 52,
      code: 'public class Counter {\n    private volatile int count = 0;\n    public void inc() { count++; }\n    public int get() { return count; }\n}',
      aiComment: 'volatile 无法保证 count++ 的原子性，压测结果必然小于预期值，属于本章核心误区。',
      teacherComment: '',
      rubricScores: { '功能正确性': 12, '并发安全分析': 8, '性能对比与结论': 8, '代码规范与注释': 8 }
    },
    {
      id: 'sb-6', asId: 'as-2', sid: 's07', name: '孙悦', status: 'pending',
      submittedAt: '09-27 20:33', attempts: 2, similarity: 13, timeSlot: '正常时段',
      score: null, aiScore: 85,
      code: '// ThreadPoolExecutor 自定义 + 4 种拒绝策略对比表\nnew ThreadPoolExecutor(4, 8, 60, TimeUnit.SECONDS,\n        new ArrayBlockingQueue<>(200),\n        Executors.defaultThreadFactory(),\n        new ThreadPoolExecutor.CallerRunsPolicy());',
      aiComment: '拒绝策略选择了 CallerRunsPolicy 并给出理由，理解到位。队列容量取值依据可再说明。',
      teacherComment: '',
      rubricScores: { '功能正确性': 35, '并发安全分析': 22, '性能对比与结论': 16, '代码规范与注释': 12 }
    },
    {
      id: 'sb-7', asId: 'as-2', sid: 's12', name: '蒋楠', status: 'pending',
      submittedAt: '09-28 02:16', attempts: 6, similarity: 87, timeSlot: '凌晨时段',
      score: null, aiScore: 70,
      code: '// 与刘佳怡（s05）提交内容高度一致，变量命名与注释结构雷同\npublic class LRUCache<K, V> { /* ... */ }',
      aiComment: '相似度检测 87%，与 s05 提交高度雷同；且提交于凌晨、6 次重复提交，行为异常。',
      teacherComment: '',
      rubricScores: { '功能正确性': 30, '并发安全分析': 14, '性能对比与结论': 12, '代码规范与注释': 10 }
    },
    {
      id: 'sb-8', asId: 'as-1', sid: 's06', name: '赵启明', status: 'missing',
      submittedAt: null, attempts: 0, similarity: 0, timeSlot: '-',
      score: null, aiScore: null, code: '', aiComment: '', teacherComment: '',
      rubricScores: {}
    },
    {
      id: 'sb-9', asId: 'as-1', sid: 's10', name: '郑浩', status: 'missing',
      submittedAt: null, attempts: 0, similarity: 0, timeSlot: '-',
      score: null, aiScore: null, code: '', aiComment: '', teacherComment: '',
      rubricScores: {}
    }
  ],

  /* ---------- 预警名单（学情 Agent 生成） ---------- */
  warnings: [
    { id: 'wn-1', level: 'high', type: '连续未登录', sid: 's06', name: '赵启明',
      desc: '连续 5 天未登录，作业提交率从 75% 降至 42%，近 7 日活跃趋势归零。', time: '09-28 08:00', handled: false },
    { id: 'wn-2', level: 'high', type: '提交次数骤降', sid: 's03', name: '王雨欣',
      desc: '近 3 天未登录，练习提交次数下降 80%，并发编程章节连续 4 次不通过。', time: '09-28 08:00', handled: false },
    { id: 'wn-3', level: 'mid', type: '卡点知识点', sid: 's10', name: '郑浩',
      desc: '并发编程连续 4 次练习不通过且未申请提示，判定为「卡死」而非「偷懒」。', time: '09-27 08:00', handled: false },
    { id: 'wn-4', level: 'mid', type: '非正常时段提交', sid: 's08', name: '周凯',
      desc: '近 7 日有 6 次提交发生在 00:00-04:00，可能存在赶工或代做风险。', time: '09-27 08:00', handled: false },
    { id: 'wn-5', level: 'high', type: '代码相似度异常', sid: 's12', name: '蒋楠',
      desc: '本次提交与 s05 相似度 87%，且 30 分钟内重复提交 6 次，建议人工复核。', time: '09-28 11:20', handled: false },
    { id: 'wn-6', level: 'mid', type: '班级共性卡点', sid: null, name: '全班',
      desc: '并发编程班级平均掌握度 34 分，卡点集中在「内存可见性」与「线程池参数配置」，建议下节课复盘。', time: '09-27 08:00', handled: false }
  ],

  /* ---------- 班级卡点知识点（学情看板） ---------- */
  classStuckPoints: [
    { kp: 'concurrent', name: '并发编程', avg: 34, stuckRate: 68 },
    { kp: 'jvm',        name: 'JVM 与内存模型', avg: 39, stuckRate: 57 },
    { kp: 'generic',    name: '泛型与反射', avg: 43, stuckRate: 49 },
    { kp: 'collection', name: '集合框架', avg: 47, stuckRate: 41 },
    { kp: 'spring',     name: 'Spring Boot 基础', avg: 49, stuckRate: 36 },
    { kp: 'algo',       name: '数据结构与算法', avg: 52, stuckRate: 30 }
  ],

  /* ---------- 快捷指令芯片 ---------- */
  quickPrompts: [
    { label: '出一道变式题', text: '这个知识点再出一道变式题练练' },
    { label: '报错解释', text: '这段代码报错了，帮我看看原因' },
    { label: '代码审查', text: '帮我审查一下这段代码的规范性问题' },
    { label: '生成学习路径', text: '帮我重新规划这个知识点的学习路径' },
    { label: '我的薄弱点', text: '我现在最该补哪个知识点' },
    { label: '今日复习', text: '把我今天的复习队列推给我' }
  ],

  /* ---------- 右侧上下文：代码片段 ---------- */
  snippets: [
    { id: 'sn-1', lang: 'java', from: 'se-2 · HashMap#resize', title: 'HashMap 元素迁移',
      code: 'if ((e.hash & oldCap) == 0) {\n    loTail.next = e;   // 留在原桶\n} else {\n    hiTail.next = e;   // 迁移到 index + oldCap\n}' },
    { id: 'sn-2', lang: 'java', from: 'se-1 · 并发计数器', title: 'AtomicInteger vs LongAdder',
      code: 'private final AtomicInteger ai = new AtomicInteger();\nprivate final LongAdder la = new LongAdder();\n\nvoid incAtomic() { ai.incrementAndGet(); }   // 高竞争下 CAS 自旋开销大\nvoid incAdder()  { la.increment(); }        // 分段累加，写多读少场景更优' },
    { id: 'sn-3', lang: 'java', from: '作业 as-2 · 我的提交', title: '线程池构造',
      code: 'new ThreadPoolExecutor(\n    4, 8, 60, TimeUnit.SECONDS,\n    new ArrayBlockingQueue<>(200),\n    Executors.defaultThreadFactory(),\n    new ThreadPoolExecutor.CallerRunsPolicy());' }
  ]
};
