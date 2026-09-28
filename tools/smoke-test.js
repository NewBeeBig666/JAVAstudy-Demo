/* 冒烟测试：用 jsdom 真实加载 index.html，走通两条核心流程
 * 运行：node tools/smoke-test.js
 */
const path = require('path');
const { JSDOM, VirtualConsole } = require('jsdom');

const root = path.resolve(__dirname, '..');
const file = path.join(root, 'index.html');

const errors = [];
const vc = new VirtualConsole();
vc.on('jsdomError', e => errors.push('jsdomError: ' + (e.stack || e.message)));
vc.on('error', (...a) => errors.push('console.error: ' + a.join(' ')));

const sleep = ms => new Promise(r => setTimeout(r, ms));
const results = [];
function check(name, ok, extra) {
  results.push((ok ? 'PASS  ' : 'FAIL  ') + name + (extra ? '  → ' + extra : ''));
}

(async () => {
  const dom = await JSDOM.fromFile(file, {
    runScripts: 'dangerously',
    resources: 'usable',
    pretendToBeVisual: true,
    virtualConsole: vc,
    url: 'file:///' + file.replace(/\\/g, '/')
  });
  const { window } = dom;
  const doc = window.document;
  await sleep(1200);

  const $ = s => doc.querySelector(s);
  const txt = s => ($(s) ? $(s).textContent : '');
  const click = async (sel, wait) => {
    const el = typeof sel === 'string' ? $(sel) : sel;
    if (!el) { results.push('FAIL  找不到元素: ' + sel); return false; }
    el.dispatchEvent(new window.MouseEvent('click', { bubbles: true }));
    await sleep(wait || 700);
    return true;
  };
  const clickAct = async (act, val, wait) => {
    const sel = '[data-act="' + act + '"]' + (val ? '[data-val="' + val + '"]' : '');
    return click(sel, wait);
  };

  /* ---------- 学生侧：初始加载 ---------- */
  check('页面加载 · 顶栏渲染', txt('#topbar').includes('Java 学习智能体'));
  check('页面加载 · Activity Bar 5 项', doc.querySelectorAll('#activity .act-btn').length >= 6);
  check('页面加载 · 会话列表', doc.querySelectorAll('#side .list-item').length >= 2);
  check('学生侧 · 对话渲染', doc.querySelectorAll('#main .msg').length >= 3, doc.querySelectorAll('#main .msg').length + ' 条消息');
  check('学生侧 · 学习路径卡片', txt('#main').includes('个性化学习路径'));
  check('学生侧 · 右侧上下文区', txt('#ctx').includes('当前知识点'));

  /* ---------- 学生流程 1：练习 → 掌握度更新 ---------- */
  const before = window.MOCK ? null : null;
  const opt = doc.querySelector('#main .opt');
  await click(opt, 200);
  check('练习 · 选项可选中', !!doc.querySelector('#main .opt.sel'));
  await click('[data-act="submitEx"]', 1400);
  check('练习 · 提交后返回即时评测', txt('#main').includes('即时评测') || txt('#main').includes('掌握度'));

  /* ---------- 学生流程 2：新建会话 ---------- */
  await clickAct('newSession', null, 300);
  check('新建会话 · 弹窗打开', !!$('.mask') && txt('.mask').includes('选择知识点'));
  await click('[data-act="pickKp"][data-val="generic"]', 200);
  await clickAct('createSession', null, 2600);
  check('新建会话 · 诊断 Agent 输出', txt('#main').includes('诊断结果'));
  check('新建会话 · 规划 Agent 生成路径', txt('#main').includes('泛型与反射'));

  /* ---------- 学生：知识图谱 / 作业 / 报告 ---------- */
  await clickAct('section', 'knowledge', 900);
  check('知识图谱 · 热力图', doc.querySelectorAll('#main .kp-cell').length === 12);
  check('知识图谱 · 依赖链溯源', txt('#main').includes('依赖链溯源'));
  await clickAct('section', 'homework', 900);
  check('我的作业 · 已批改反馈回流', txt('#main').includes('教师反馈'));
  await clickAct('section', 'exercise', 900);
  check('练习记录 · 有数据', doc.querySelectorAll('#main .table tbody tr').length >= 1);
  await clickAct('section', 'report', 900);
  check('画像与报告 · 概览 KPI', txt('#main').includes('连续学习'));

  /* ---------- 切换到教师视角 ---------- */
  await clickAct('role', 'teacher', 900);
  check('教师视角 · 学情看板', txt('#main').includes('班级学情看板'));
  check('教师视角 · KPI 卡片', doc.querySelectorAll('#main .kpi').length === 4);

  /* ---------- 教师流程：学情 Agent 报告 ---------- */
  await clickAct('runInsight', null, 3200);
  check('学情Agent · 报告生成完成', txt('#main').includes('班级薄弱点报告') && txt('#main').includes('教学建议'));

  /* ---------- 教师流程：发布作业 ---------- */
  await clickAct('section', 'assignments', 900);
  await clickAct('publishModal', null, 300);
  check('发布作业 · 弹窗打开', !!$('.mask') && txt('.mask').includes('发布新作业'));
  const inputs = doc.querySelectorAll('.modal input.input');
  inputs[0].value = 'JUnit5 参数化测试练习';
  inputs[0].dispatchEvent(new window.Event('input', { bubbles: true }));
  await click('[data-act="pubKp"][data-val="test"]', 200);
  await clickAct('doPublish', null, 700);
  check('发布作业 · 新作业出现在列表', txt('#main').includes('JUnit5 参数化测试练习'));

  /* ---------- 教师流程：查看提交 → 评分 ---------- */
  await clickAct('section', 'submissions', 900);
  check('提交批改 · 列表渲染', doc.querySelectorAll('#main .table tbody tr').length >= 3);
  const row = doc.querySelector('#main .table tbody tr');
  await click(row, 400);
  check('批改抽屉 · 打开', !!$('.drawer') && txt('.drawer').includes('Rubric 评分'));
  await clickAct('useAi', null, 300);
  await clickAct('doGrade', null, 700);
  check('评分发布 · 抽屉关闭', !$('.drawer'));
  check('评分发布 · 状态变已批改', (txt('#main').match(/已批改/g) || []).length >= 1);

  /* ---------- 教师流程：预警处理 ---------- */
  await clickAct('section', 'warnings', 900);
  const wcount = doc.querySelectorAll('#main [data-act="warnHandle"]').length;
  check('预警中心 · 待处理列表', wcount >= 4, wcount + ' 条');
  if (wcount) await click('[data-act="warnHandle"]', 400);
  check('预警中心 · 标记已处理生效', doc.querySelector('#main .badge.ok') !== null);

  /* ---------- 回流验证：切回学生视角 ---------- */
  await clickAct('role', 'student', 900);
  await clickAct('section', 'homework', 900);
  check('回流 · 学生侧看到新作业', txt('#main').includes('JUnit5 参数化测试练习'));
  const hasGraded = (txt('#main').match(/教师反馈/g) || []).length;
  check('回流 · 学生侧看到批改反馈', hasGraded >= 1, hasGraded + ' 份');

  /* ---------- 空状态 ---------- */
  await clickAct('settings', null, 300);
  await clickAct('clearSessions', null, 500);
  await clickAct('section', 'chat', 900);
  check('空状态 · 清空会话后提示', txt('#main').includes('还没有学习会话'));

  console.log(results.join('\n'));
  const failed = results.filter(r => r.startsWith('FAIL'));
  console.log('\n---- ' + (results.length - failed.length) + '/' + results.length + ' 通过 ----');
  if (errors.length) {
    console.log('\n运行时错误:');
    console.log(errors.slice(0, 12).join('\n'));
  }
  process.exit(failed.length || errors.length ? 1 : 0);
})();
