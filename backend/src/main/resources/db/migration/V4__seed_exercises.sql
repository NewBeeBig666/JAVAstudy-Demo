-- V4: 题库种子（35 题，覆盖教学大纲 11 单元；含三级渐进提示；密码答案服务端校验）
-- 注意：JSON 内换行用 \\n 表示

INSERT INTO `exercise` (id, kp_id, type, difficulty, stem, code, options, answer, analysis, hints, mastery_delta) VALUES

-- ===== 单元1 Java概述 (kp-intro id=101) =====
(1, 101, 'CHOICE', '简单', 'Java 实现“一次编写，到处运行”的根本原因是？', NULL,
 '["Java 编译器会把源码直接编译成各平台的机器码","Java 源码编译为平台无关的字节码，由各平台的 JVM 解释/编译执行","Java 程序运行时不需要操作系统","每个平台都有自己独立的 Java 语言规范"]',
 1, 'Java 源码（.java）被编译成平台无关的字节码（.class），字节码由部署在 不同操作系统上的 JVM 负责执行。JVM 屏蔽了底层差异，这才是跨平台的本质——依赖的是 JVM，而不是源码本身。',
 '["回忆：javac 的产物是什么文件？它和 CPU 能直接执行的机器码是一回事吗？",".class 文件在 Windows 和 Linux 上内容有差别吗？","谁负责把字节码翻译成 Windows/Linux 各自的机器指令？"]', 5),

(2, 101, 'CHOICE', '简单', '关于 JDK、JRE、JVM 三者的关系，下列说法正确的是？', NULL,
 '["JDK = JRE + 开发工具（javac、jdb 等）；JRE = JVM + 核心类库","JRE = JDK + JVM","JVM 包含 JRE 和 JDK","三者相互独立，没有包含关系"]',
 0, 'JDK 面向开发者 = JRE + 编译器等开发工具；JRE 面向使用者 = JVM + 核心类库（如 java.lang、java.util）。范围上 JDK 最大，JVM 最小。',
 '["开发一个 Java 程序和运行一个 Java 程序，各自最少需要什么？","javac 命令属于运行环境还是开发工具？","JRE 里除了 JVM 还得有什么，main 方法才能跑起来？"]', 5),

-- ===== 单元2 基本语法/变量/运算符 (102-104) =====
(3, 102, 'CHOICE', '简单', '下列哪个是合法的 Java 标识符？', NULL,
 '["2score","my-name","_value","class"]',
 2, '标识符规则：不能以数字开头、不能包含连字符、不能使用关键字。_value 以下划线开头合法；2score 数字开头非法；my-name 含非法字符 -；class 是关键字。',
 '["标识符可以以哪些字符开头？数字可以吗？","连字符 - 在标识符里允许吗？它会被解析成什么？","class 在 Java 里是什么身份？"]', 5),

(4, 103, 'CHOICE', '简单', '下列代码的输出结果是？\nbyte b = (byte) 130;', NULL,
 '["130","-126","编译错误","0"]',
 1, 'byte 范围是 -128~127。130 强转后发生溢出：130 的二进制低 8 位是 1000 0010，最高位为符号位 1，表示负数，按补码还原为 -126。强制类型转换可能丢失精度，要警惕溢出。',
 '["byte 的取值范围是多少？130 超出了吗？","写出 130 的二进制，只保留低 8 位后，最高位是 0 还是 1？","补码 1000 0010 表示的十进制数是多少？"]', 6),

(5, 103, 'CHOICE', '中等', '关于八种基本数据类型的默认值，下列说法正确的是？', NULL,
 '["int 默认值是 0，boolean 默认值是 false","局部变量也有默认值，可以直接使用","所有引用类型的默认值是 null，基本类型默认值都是 0","char 没有默认值"]',
 0, '成员变量有默认值：数值型 0、boolean false、引用 null。但局部变量没有默认值，未初始化就使用会编译报错——这是高频考点。',
 '["先想：成员变量和局部变量在初始化上的待遇一样吗？","int i; 在方法里直接 System.out.println(i) 会怎样？","布尔型和数值型的默认值分别是？"]', 5),

(6, 104, 'CHOICE', '中等', '下列代码的输出结果是？\nint x = 5;\nboolean r = x > 3 || ++x > 10;\nSystem.out.println(x);', NULL,
 '["5","6","编译错误","不确定"]',
 0, '|| 是短路或：左边 x>3 为 true 时右边不再执行，所以 ++x 没有发生，x 仍为 5。短路特性（&& 左假跳右、|| 左真跳右）常被用来避免空指针判断。',
 '["|| 运算符什么时候会跳过右边的表达式？","左边 x>3 的结果是什么？右边还有机会执行吗？","如果换成单个 |（非短路或），x 会变成多少？"]', 6),

-- ===== 单元3 结构化程序设计 (105-109) =====
(7, 105, 'CHOICE', '简单', '关于 switch 语句，下列说法错误的是？', NULL,
 '["case 后面的值必须是常量","break 缺失时会继续执行下一个 case（case 穿透）","switch 的表达式类型可以是 String","default 分支必须写在所有 case 之后，否则编译错误"]',
 3, 'default 可以写在任意位置，甚至 case 中间，匹配不到任何 case 时执行。其余三项均为正确说法。JDK 7 起 switch 支持 String。',
 '["回忆：default 的位置是强制的吗？","case 穿透发生在什么情况下？","switch 支持哪些类型：int、String、double？"]', 5),

(8, 106, 'CHOICE', '中等', '下列代码循环体会执行几次？\nint i = 0;\ndo { i++; } while (i < 0);', NULL,
 '["0 次","1 次","无限次","编译错误"]',
 1, 'do-while 先执行循环体再判断条件，因此至少执行 1 次。即使初始条件 i<0 为 false，第一次循环体也已经执行过了。这与 while 的“先判断后执行”形成对比。',
 '["do-while 和 while 的判断时机有什么不同？","第一次执行循环体之前，条件被检查过吗？","循环体里 i 变成了几？此时 while(i<0) 还成立吗？"]', 5),

(9, 107, 'CHOICE', '简单', '下列代码的输出结果是？\nfor (int i = 1; i <= 5; i++) {\n    if (i == 3) continue;\n    System.out.print(i);\n}', NULL,
 '["1245","12345","12","124"]',
 0, 'continue 终止本次循环体剩余部分，直接进入下一次迭代：i=3 时跳过打印。最终输出 1245。break 则会直接结束整个循环。',
 '["continue 跳过的是这一次还是整个循环？","i=3 时 print 执行了吗？其他 i 呢？","如果换成 break，输出会是什么？"]', 5),

(10, 108, 'CHOICE', '中等', '执行下列代码会发生什么？\nint[] a = new int[3];\nSystem.out.println(a[3]);', NULL,
 '["输出 0","输出 null","抛出 ArrayIndexOutOfBoundsException","编译错误"]',
 2, '数组长度为 3，合法下标是 0、1、2。a[3] 越界，运行时抛出 ArrayIndexOutOfBoundsException。数组越界是运行时异常，编译期不检查。',
 '["长度为 3 的数组，最大合法下标是多少？","越界是编译错误还是运行时异常？","new int[3] 的元素默认值是什么？如果访问 a[2] 呢？"]', 6),

(11, 109, 'CHOICE', '中等', '下列哪组方法构成合法的重载（Overload）？', NULL,
 '["int add(int a, int b) 与 double add(int a, int b)","void add(int a) 与 void add(int b)","int add(int a, int b) 与 int add(int a, int b, int c)","int add(int a) 与 int sum(int a)"]',
 2, '重载要求方法名相同、参数列表不同（个数或类型）。返回值类型不同不构成重载；参数名不同不构成重载；方法名不同更是无关。C 项参数个数不同，合法。',
 '["重载的判定条件是什么：方法名？返回值？参数列表？","A 项只有返回值不同，编译器能区分调用哪个吗？","参数个数不同可以构成重载吗？"]', 6),

(12, 108, 'CHOICE', '困难', '关于二维数组 int[][] arr = new int[3][4]; 下列说法正确的是？', NULL,
 '["arr.length 是 4，arr[0].length 是 3","arr.length 是 3，arr[0].length 是 4","arr 共有 12 个独立的 int 变量组成的一维结构","arr[3] 是最后一行"]',
 1, 'Java 二维数组本质是“数组的数组”：arr 是长度 3 的数组，每个元素又是长度 4 的数组。arr.length=3（行数），arr[0].length=4（列数）。',
 '["Java 的二维数组其实是一维数组的什么？","arr 存的是 3 个什么类型的元素？","arr[0] 本身又是一个什么？它的 length 是多少？"]', 7),

-- ===== 单元4 类与对象 (110-114) =====
(13, 112, 'CHOICE', '简单', '封装的核心做法是？', NULL,
 '["把成员变量设为 private，提供 public 的 getter/setter 访问","把所有成员都设为 public 方便访问","用 static 修饰所有变量","尽量少定义方法"]',
 0, '封装 = 隐藏实现细节 + 暴露受控的公共接口：成员变量私有化，通过公有的 getter/setter 访问，可在其中加入校验逻辑，保护数据安全。',
 '["private 修饰后谁能访问？外部怎么办？","getter/setter 里能不能加参数校验？","开放直接访问变量和走方法访问，哪个可控？"]', 5),

(14, 113, 'CHOICE', '中等', '关于构造方法，下列说法错误的是？', NULL,
 '["构造方法名必须与类名相同","构造方法没有返回值类型（连 void 都不能写)","如果一个类没写构造方法，编译器会自动生成无参构造","构造方法不能重载"]',
 3, '构造方法可以重载（无参/有参），这是初始化对象的常用手段。一旦手写了有参构造，默认无参构造就不再自动生成——需要时必须显式写出。',
 '["构造方法能写几个？","自己写了有参构造后，无参构造还存在吗？","new Student() 时调用的是什么？"]', 5),

(15, 113, 'CHOICE', '中等', '在构造方法中调用另一个重载的构造方法，应使用？', NULL,
 '["this.构造方法名(参数)","this(参数)","super(参数)","new 类名(参数)"]',
 1, 'this(参数) 用于在构造方法中调用同类其他构造方法，且必须是构造方法的第一条语句。this.xxx 访问成员，super(参数) 调用父类构造。',
 '["this 的三种用法分别是什么？","哪一种专门用于构造方法之间的调用？","这种调用在构造方法里对位置有什么要求？"]', 6),

(16, 114, 'CHOICE', '中等', '下列代码的输出结果是？\nclass Counter {\n    static int count;\n    Counter() { count++; }\n}\nnew Counter(); new Counter(); new Counter();\nSystem.out.println(Counter.count);', NULL,
 '["0","1","3","编译错误"]',
 2, 'static 变量被类的所有实例共享，存放在方法区（JDK 8 后为元空间）。每 new 一次构造方法执行 count++，三次实例化后 count=3，且通过类名直接访问。',
 '["static 变量属于对象还是类？","三个对象各自有一份 count 吗？","构造方法每执行一次发生什么？"]', 6),

(17, 114, 'CHOICE', '中等', '下列代码哪一行会编译错误？\nclass Demo {\n    static int s = 1;\n    int i = 2;\n    void run() { int x = s + i; }\n    static void go() { int y = s + i; }\n}', NULL,
 '["run 方法里的 x = s + i","go 方法里的 y = s + i","两行都会报错","都不会报错"]',
 1, '静态方法 go() 中没有 this 引用，不能直接访问非静态成员 i（属于具体对象）。静态方法只能直接访问静态成员；反之实例方法访问两者皆可。',
 '["静态方法执行时，存在当前对象吗？","i 这个变量属于谁？没有对象时能定位它吗？","实例方法 run() 里访问 s 和 i 有问题吗？"]', 6),

(18, 111, 'CHOICE', '中等', '下列代码的输出结果是？\nclass P { int v = 10; }\nP a = new P();\nP b = a;\nb.v = 20;\nSystem.out.println(a.v);', NULL,
 '["10","20","null","编译错误"]',
 1, 'P b = a 并没有创建新对象，只是让 b 和 a 指向同一个对象。通过 b 修改 v，a 看到的也是 20。Java 对象赋值传递的是引用（地址值）。',
 '["b = a 这一步内存里新开了对象吗？","现在有几个引用指向那个对象？","b.v = 20 改的是谁的 v？"]', 6),

-- ===== 单元5 继承/多态/抽象类/接口 (115-118) =====
(19, 115, 'CHOICE', '中等', '关于方法重写（Override），下列说法错误的是？', NULL,
 '["子类重写方法的访问权限不能小于父类方法","子类重写方法的返回值必须与父类相同或是其子类","重写发生在父类与子类之间，与重载是不同概念","private 方法也可以被重写"]',
 3, 'private 方法对子类不可见，谈不上重写。重写遵循“两同两小一大”：方法名参数相同，返回值和异常范围更小或相同，访问权限更大或相同，并可用 @Override 注解校验。',
 '["private 成员子类能看见吗？","@Override 注解的作用是什么？","重写和重载的区别用一句话概括？"]', 6),

(20, 115, 'CHOICE', '中等', '下列代码的输出结果是？\nclass A { void hi() { System.out.print("A"); } }\nclass B extends A { void hi() { System.out.print("B"); } }\nA obj = new B();\nobj.hi();', NULL,
 '["A","B","AB","编译错误"]',
 1, '多态的核心：编译看左边（A 有 hi 方法即可通过编译），运行看右边（实际执行 B 重写后的 hi）。输出 B。',
 '["obj 的静态类型和实际类型分别是什么？","方法调用在编译期检查谁，在运行期执行谁？","如果 B 没有重写 hi，输出会是什么？"]', 7),

(21, 118, 'CHOICE', '中等', '“饲养员给小狗喂骨头小狗汪汪叫，给小猫喂小鱼小猫喵喵叫”，最适合利用多态的实现方式是？', NULL,
 '["在饲养员类中用 if-else 判断动物类型分别调用不同方法","定义 Animal 択象类/接口含 makeSound()，Dog 和 Cat 各自重写，饲养员面向 Animal 编程","为每种动物写一个独立的喂食方法","把叫声写在一个大方法里用字符串区分"]',
 1, '面向 Animal 抽象编程：新增动物只需新增子类，饲养员代码零修改——这正是多态“易扩展、易维护”的价值，也是开闭原则的体现。',
 '["新增一种动物时，if-else 方案要改几处代码？","把“会叫”这个行为抽象到哪里最合适？","饲养员应该依赖具体类还是抽象？"]', 7),

(22, 118, 'CHOICE', '中等', '下列代码的输出结果是？\nObject obj = "hello";\nif (obj instanceof String s) {\n    System.out.println(s.length());\n} else {\n    System.out.println("not string");\n}', NULL,
 '["5","not string","编译错误","抛出异常"]',
 0, 'obj 实际指向字符串 "hello"，instanceof 判断为 true。Java 16+ 的模式匹配写法（instanceof String s）可直接绑定变量，等价于先判断再强转。输出 5。',
 '["obj 的编译类型和运行类型分别是什么？","instanceof 判断的是哪一侧的类型？","模式匹配绑定出的 s 是什么类型，还需要强转吗？"]', 6),

(23, 116, 'CHOICE', '困难', '关于抽象类和接口，下列说法正确的是？', NULL,
 '["抽象类可以有构造方法，接口不能有","抽象类和接口都可以被实例化","一个类只能实现一个接口","接口中不能定义任何有方法体的方法"]',
 0, '抽象类的构造方法供子类 super() 调用完成初始化，但两者都不能直接 new。类可以实现多个接口（JDK 8+ 接口还允许 default/static 默认方法）。单继承、多实现是两者的核心区别。',
 '["抽象类的构造方法是给谁用的？","一个类能继承几个抽象类？能实现几个接口？","JDK 8 之后接口里能写带方法体的方法吗？"]', 7),

-- ===== 单元6 异常 (119) =====
(24, 119, 'CODE', '中等', '下面这段代码的返回值是什么？请说明原因。', 'public static int test() {\n    int i = 1;\n    try {\n        return ++i;\n    } finally {\n        return i++;\n    }\n}',
 '["返回 1","返回 2","返回 3","编译错误"]',
 1, 'try 中执行 ++i 后 i=2，return 的值 2 会被暂存；但 finally 中的 return 会覆盖 try 的返回出口，此时执行 i++ 表达式的值为 2（后置自增先返回旧值），所以最终返回 2。结论：永远不要在 finally 中写 return，它会吞掉异常并覆盖正常返回值。',
 '["先分清 ++i（先增后取值）与 i++（先取值后增）的差别。","再想 finally 的执行时机：是在 return「保存返回值之后、方法真正返回之前」。","关键点：finally 里的 return 会直接替换掉 try 中已经暂存好的返回出口。"]', 6),

(25, 119, 'CODE', '中等', '接收用户输入的分数（0-100 之外视为非法），最适合的做法是？', 'int score = readScore();\n// 分数必须在 0-100 之间',
 '["用 System.exit(1) 直接退出程序","定义 ScoreOutOfBoundsException，非法时 throw 该异常对象，调用方 try-catch 处理","把分数悄悄改成 0 继续运行","打印一句警告后继续使用非法分数"]',
 1, '自定义异常 + throw 是标准做法：把“非法输入”以异常对象的形式向上传递，由调用方决定如何处理。throw 用于方法体内抛出，throws 用于方法签名上声明。悄悄容错会掩盖错误。',
 '["异常处理的本质是“报告问题”还是“掩盖问题”？","throw 和 throws 分别用在哪里？","自定义异常要继承哪个类：Exception 还是 RuntimeException？"]', 6),

(26, 119, 'CHOICE', '中等', '关于 try-with-resources，下列说法正确的是？', NULL,
 '["任何类都可以放进 try() 中自动关闭","实现 AutoCloseable 接口的资源可以放进 try()，语句结束自动调用 close()","它不能替代 finally 关闭资源","资源关闭顺序与声明顺序相同"]',
 1, 'try-with-resources 要求资源实现 AutoCloseable。多个资源的关闭顺序与声明顺序相反（后声明先关闭）。相比 finally 手动关闭，代码更简洁且能避免异常 suppressed 问题。',
 '["try() 括号里的资源需要实现什么接口？","声明了两个资源，谁先被关闭？","和 finally 里手写 close 相比，它好在哪？"]', 6),

-- ===== 单元7 内部类/Lambda/泛型/Stream (120,122,123,125) =====
(27, 120, 'CHOICE', '中等', '下列代码能否编译？为什么？\nRunnable r = new Runnable() {\n    @Override\n    public void run() { System.out.println("run"); }\n};', NULL,
 '["能，这是匿名内部类实现接口的典型写法","不能，接口不能用 new 实例化","不能，Runnable 没有抽象方法","能，但 run 方法不会执行"]',
 0, 'new Runnable(){...} 创建的是匿名内部类（实现了 Runnable 的无名子类）的实例，并非直接实例化接口。这是事件回调、线程任务的经典写法，JDK 8 后可简化为 Lambda：Runnable r = () -> ...。',
 '["new 后面的花括号里在定义什么？","这个无名类和 Runnable 是什么关系？","如果改写成 Lambda，等价形式是什么？"]', 6),

(28, 122, 'CHOICE', '中等', '下列哪个是合法的 Lambda 表达式（对应接口 Comparator<String>）？', NULL,
 '["(a, b) -> a.length() - b.length()","a, b => a.length() - b.length()","function(a, b) { return a.length() - b.length(); }","(a, b) -> { a.length() - b.length() }"]',
 0, 'Java Lambda 语法：参数 -> 表达式，或 (参数) -> { 语句; return 值; }。D 项花括号内是表达式不是语句且缺 return。=> 是 C#/Kotlin 的写法。Lambda 的目标类型必须是函数式接口（仅一个抽象方法）。',
 '["Lambda 的箭头用的是什么符号？","表达式形式和方法体形式（带花括号）各有什么要求？","Comparator 只有一个抽象方法吗？compare？"]', 6),

(29, 123, 'CHOICE', '困难', '关于 Java 泛型的类型擦除，下列说法正确的是？', NULL,
 '["运行时可以通过 list.getClass() 拿到 List<String> 的完整泛型信息","无界泛型 T 在字节码中被擦除为 Object，有上界 T extends Number 被擦除为 Number","泛型信息完全不写入 class 文件","反射可以在运行时修改 List<Integer> 的泛型参数"]',
 1, '泛型是编译期语法糖：无界 T 擦除为 Object，有上界擦除为上界类型；擦除后编译器自动插入强制转型与桥接方法。签名信息（Signature 属性）保留在 class 文件中供反射读取，但不参与运行时类型校验。',
 '["先想：new ArrayList<String>() 与 new ArrayList<Integer>() 的 getClass() 返回什么？","既然擦除了，为什么调用 get() 时不需要手写强转？","区分「字节码中的 Signature 属性」与「运行时的实际类型约束」。"]', 8),

(30, 125, 'CHOICE', '中等', '下列 Stream 操作后 list 的内容是？\nList<String> list = List.of("apple", "banana", "avocado");\nList<String> r = list.stream()\n    .filter(s -> s.startsWith("a"))\n    .map(String::toUpperCase)\n    .collect(Collectors.toList());', NULL,
 '["[APPLE, AVOCADO]","[apple, avocado]","[APPLE, BANANA, AVOCADO]","[BANANA]"]',
 0, 'filter 过滤出以 a 开头的 apple、avocado，map 转大写，collect 收集为新 List：[APPLE, AVOCADO]。Stream 是惰性求值，不修改原集合，collect 才触发执行。',
 '["filter 保留的是什么条件的数据？","map 做的是转换还是过滤？","原 list 会被修改吗？什么操作才会触发流水线执行？"]', 6),

-- ===== 单元8 Java常用API (121) =====
(31, 121, 'CHOICE', '简单', '需要在一个循环中拼接大量字符串，应优先使用？', NULL,
 '["String 用 + 拼接","StringBuilder 的 append()","String 的 concat()","char 数组手动拼接"]',
 1, 'String 不可变，循环中 + 拼接每次都产生新对象，效率低。StringBuilder 是可变字符序列，append 原地修改，循环拼接的首选。线程安全场景才考虑 StringBuffer。',
 '["String 对象创建之后能改内容吗？","循环 1 万次 + 拼接会产生多少个中间对象？","StringBuilder 和 StringBuffer 的区别是什么？"]', 5),

(32, 121, 'CHOICE', '中等', '下列代码的输出结果是？\nInteger a = 127, b = 127;\nInteger c = 128, d = 128;\nSystem.out.print((a == b) + " ");\nSystem.out.println(c == d);', NULL,
 '["true true","false false","true false","false true"]',
 2, 'Integer 缓存了 -128~127 的对象：a、b 指向同一缓存对象，== 为 true；128 超出缓存范围，c、d 是不同对象，== 为 false。包装类比较值务必用 equals。',
 '["自动装箱调用的是哪个方法：valueOf？","valueOf 对小整数做了什么优化，缓存范围多大？","包装类型比较内容应该用什么方式？"]', 6),

-- ===== 单元9 集合 (124) =====
(33, 124, 'CHOICE', '中等', '关于 HashMap 的说法，正确的是？', NULL,
 '["HashMap 允许 null 键和 null 值，且键唯一","HashMap 保证迭代顺序与插入顺序一致","HashMap 是线程安全的","HashMap 的 key 必须实现 Comparable"]',
 0, 'HashMap 允许一个 null 键和多个 null 值；不保证任何顺序（有序需求用 LinkedHashMap）；非线程安全（并发场景用 ConcurrentHashMap）；key 靠 hashCode+equals 定位，无需 Comparable（TreeMap 才需要）。',
 '["null 键在 HashMap 里怎么定位桶？","想把插入顺序记住应该换哪个实现类？","两个线程同时 put 会发生什么？"]', 6),

(34, 124, 'CHOICE', '简单', '需要存储不重复的元素并快速判断存在性，应选择？', NULL,
 '["ArrayList","HashSet","LinkedList","HashMap 的 key 概念之外再套一层 List"]',
 1, 'HashSet 基于 HashMap 的 key 实现，元素去重，contains 判断接近 O(1)。List 的 contains 是 O(n)。这是“数据去重”类需求的标准答案。',
 '["去重本质上是在判断什么：equals 还是 ==？","HashSet 底层借用了谁的能力？","ArrayList.contains 每次要扫描多少元素？"]', 5),

-- ===== 单元10 I/O流 (126) =====
(35, 126, 'CHOICE', '中等', '要把一个文本文件按行读取并高效处理，最合适的组合是？', NULL,
 '["FileInputStream + 逐字节读取","FileReader + BufferedReader 的 readLine()","FileWriter + flush()","ObjectInputStream 直接读对象"]',
 1, '文本文件读字符流 FileReader，套缓冲流 BufferedReader 提升效率且提供 readLine() 按行读取。字节流适合图片/音视频等二进制；对象流用于序列化场景。',
 '["文本文件属于字符数据还是二进制数据？","缓冲流的代价很小，收益是什么？","readLine 是谁提供的方法，FileReader 自己有吗？"]', 6),

-- ===== 单元11 JDBC (127) =====
(36, 127, 'CHOICE', '简单', '关于使用 PreparedStatement 的说法，正确的是？', NULL,
 '["PreparedStatement 只能防 SQL 注入，对性能没有帮助","PreparedStatement 会预编译 SQL 模板，既能防注入，也能在批量执行时减少解析开销","只要用了 PreparedStatement 就一定能用上数据库的查询缓存","参数占位符可以直接拼接表名和 ORDER BY 字段"]',
 1, 'PreparedStatement 先把 SQL 模板发给数据库预编译，参数以绑定变量方式传入，不再参与 SQL 语义解析，因此天然防注入。批量执行时复用同一模板可显著减少硬解析。但占位符只能绑定「值」，不能绑定表名、列名、排序方向等标识符。',
 '["注入的本质是什么？参数在什么情况下会被当成 SQL 语义的一部分？","预编译发生在哪一步？批量插入时模板被解析几次？","思考反例：SELECT * FROM ? 能执行吗？为什么？"]', 5),

(37, 127, 'CHOICE', '中等', 'JDBC 编程的正确步骤顺序是？', NULL,
 '["加载驱动 → 获取连接 → 执行 SQL → 处理结果集 → 释放资源","获取连接 → 加载驱动 → 处理结果集 → 执行 SQL","加载驱动 → 执行 SQL → 获取连接 → 处理结果集","获取连接 → 执行 SQL → 加载驱动 → 释放资源"]',
 0, '标准五步：Class.forName 加载驱动；DriverManager.getConnection 获取连接；createStatement/prepareStatement 执行 SQL；遍历 ResultSet 处理结果；按“后开先关”顺序关闭资源（推荐 try-with-resources）。',
 '["先有驱动才能拿到连接吗？","Statement 是用什么对象创建出来的？","结果集处理完，资源关闭的顺序和打开顺序有什么关系？"]', 6);

-- ===== 张明的历史练习记录（近 10 日，供报告趋势与错题本铺垫） =====
INSERT INTO `exercise_record` (user_id, exercise_id, kp_id, selected, is_correct, delta, hint_used, created_at) VALUES
(2, 1, 101, 1, 1, 5, 0, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(2, 3, 102, 2, 1, 5, 0, DATE_SUB(NOW(), INTERVAL 9 DAY)),
(2, 5, 103, 0, 1, 5, 0, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(2, 9, 107, 0, 1, 5, 0, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(2, 14, 113, 3, 0, -3, 0, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(2, 16, 114, 2, 1, 6, 0, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(2, 33, 124, 0, 1, 6, 1, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 29, 123, 1, 1, 8, 1, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 30, 125, 2, 0, -3, 0, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 33, 124, 0, 0, -3, 0, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 29, 123, 0, 0, -3, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 20, 115, 1, 1, 7, 0, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- ===== 张明的错题本 =====
INSERT INTO `wrong_book` (user_id, exercise_id, kp_id, wrong_count, last_wrong_at) VALUES
(2, 33, 124, 2, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 29, 123, 2, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 14, 113, 1, DATE_SUB(NOW(), INTERVAL 7 DAY));

-- ===== 张明的 SM-2 复习队列 =====
INSERT INTO `review_schedule` (user_id, exercise_id, kp_id, title, ease_factor, interval_days, repetitions, next_review_date, last_review_date, source, status) VALUES
(2, 33, 124, 'HashMap 的特性（null 键 / 顺序 / 线程安全）', 2.50, 1, 2, CURDATE(), DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'WRONG', 'PENDING'),
(2, 29, 123, '泛型的类型擦除', 2.50, 1, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'WRONG', 'PENDING'),
(2, 30, 125, 'Stream 的 filter / map / collect 流水线', 2.30, 2, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'WEAK', 'PENDING');
