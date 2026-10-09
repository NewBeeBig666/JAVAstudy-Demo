package com.la.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.la.ai.AgentPrompts;
import com.la.ai.ChatMessage;
import com.la.ai.LlmClient;
import com.la.config.DeepSeekProperties;
import com.la.entity.*;
import com.la.exception.BizException;
import com.la.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI 批改：Rubric 驱动 + Temperature=0 + JSON 容错
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GradingService {

    private final LlmClient llmClient;
    private final DeepSeekProperties props;
    private final ObjectMapper objectMapper;
    private final SubmissionMapper submissionMapper;
    private final AssignmentMapper assignmentMapper;
    private final RubricMapper rubricMapper;
    private final RubricScoreMapper rubricScoreMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;
    private final SessionService sessionService;
    private final SessionMapper sessionMapper;

    /**
     * 单份 AI 初评（同步）
     */
    @Transactional
    public Map<String, Object> aiGrade(Long submissionId) {
        Submission sub = submissionMapper.selectById(submissionId);
        if (sub == null) {
            throw new BizException("提交不存在");
        }
        Assignment as = assignmentMapper.selectById(sub.getAssignmentId());
        List<Rubric> rubrics = rubricMapper.selectList(new LambdaQueryWrapper<Rubric>()
                .eq(Rubric::getAssignmentId, as.getId()).orderByAsc(Rubric::getSortOrder));

        String prompt = buildPrompt(as, rubrics, sub);
        List<ChatMessage> messages = List.of(
                ChatMessage.system(null2(AgentPromptsHelper.loadGradingPrompt())),
                ChatMessage.user(prompt));
        String raw = llmClient.chat(messages, props.getGradeTemperature());
        AiGradeResult result = parseGradeResult(raw, rubrics);

        sub.setAiScore(result.aiScore);
        sub.setAiComment(result.comment);
        sub.setStatus("AI_REVIEWED");
        submissionMapper.updateById(sub);

        // 保存 AI 逐项评分
        rubricScoreMapper.delete(new LambdaQueryWrapper<RubricScore>()
                .eq(RubricScore::getSubmissionId, sub.getId()));
        for (Map.Entry<Long, Integer> e : result.rubricScores.entrySet()) {
            RubricScore rs = new RubricScore();
            rs.setSubmissionId(sub.getId());
            rs.setRubricId(e.getKey());
            rs.setScore(e.getValue());
            rubricScoreMapper.insert(rs);
        }

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("aiScore", result.aiScore);
        resp.put("aiComment", result.comment);
        resp.put("rubricScores", result.rubricScores.entrySet().stream()
                .collect(Collectors.toMap(e -> rubricName(rubrics, e.getKey()), Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new)));
        return resp;
    }

    /**
     * 批量 AI 初评（异步，走 gradingExecutor 线程池）
     */
    @Async("gradingExecutor")
    public void aiGradeBatch(Long assignmentId, Long teacherId) {
        List<Submission> pending = submissionMapper.selectList(new LambdaQueryWrapper<Submission>()
                .eq(Submission::getAssignmentId, assignmentId)
                .eq(Submission::getStatus, "PENDING"));
        int ok = 0;
        for (Submission sub : pending) {
            try {
                aiGrade(sub.getId());
                ok++;
            } catch (Exception e) {
                log.error("批量初评失败: submission={}", sub.getId(), e);
            }
        }
        notificationService.push(teacherId, "批量 AI 初评完成：作业 #" + assignmentId
                + " 共处理 " + ok + "/" + pending.size() + " 份。", "TEACHER");
    }

    /**
     * 教师发布评分：写回 score/rubricScores → 学生通知 + 会话 feedback 卡片回流
     */
    @Transactional
    public void teacherGrade(Long submissionId, Map<String, Integer> rubricScoresByName, String comment) {
        Submission sub = submissionMapper.selectById(submissionId);
        if (sub == null) {
            throw new BizException("提交不存在");
        }
        Assignment as = assignmentMapper.selectById(sub.getAssignmentId());
        List<Rubric> rubrics = rubricMapper.selectList(new LambdaQueryWrapper<Rubric>()
                .eq(Rubric::getAssignmentId, as.getId()).orderByAsc(Rubric::getSortOrder));

        int total = 0;
        rubricScoreMapper.delete(new LambdaQueryWrapper<RubricScore>()
                .eq(RubricScore::getSubmissionId, sub.getId()));
        for (Rubric r : rubrics) {
            Integer score = rubricScoresByName.get(r.getName());
            if (score != null) {
                int s = Math.min(score, r.getMaxScore());
                total += s;
                RubricScore rs = new RubricScore();
                rs.setSubmissionId(sub.getId());
                rs.setRubricId(r.getId());
                rs.setScore(s);
                rubricScoreMapper.insert(rs);
            }
        }
        sub.setScore(total);
        sub.setTeacherComment(comment != null ? comment : "");
        sub.setStatus("GRADED");
        sub.setGradedAt(LocalDateTime.now());
        submissionMapper.updateById(sub);

        // 学生通知
        notificationService.push(sub.getStudentId(),
                "作业「" + as.getTitle() + "」已批改：" + total + " 分。查看教师评语与 Rubric 逐项得分。", "GRADE");

        // 会话内 feedback 卡片回流（写入学生最近一个会话）
        SessionEntity latest = sessionMapper.selectOne(new LambdaQueryWrapper<SessionEntity>()
                .eq(SessionEntity::getUserId, sub.getStudentId())
                .orderByDesc(SessionEntity::getUpdatedAt).last("LIMIT 1"));
        if (latest != null) {
            Map<String, Object> card = new LinkedHashMap<>();
            card.put("type", "feedback");
            card.put("asId", String.valueOf(as.getId()));
            card.put("title", as.getTitle());
            card.put("score", total);
            card.put("comment", comment != null ? comment : "");
            sessionService.insertMessage(latest.getId(), "AGENT", "grading",
                    "你的作业「" + as.getTitle() + "」批改完成，得分 **" + total + "**。详细评语见卡片，也可在「我的作业」查看。",
                    "feedback", card);
        }
    }

    private String buildPrompt(Assignment as, List<Rubric> rubrics, Submission sub) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 作业信息\n标题：").append(as.getTitle())
                .append("\n要求：").append(null2(as.getDescription())).append('\n');
        sb.append("\n## Rubric 评分标准\n");
        for (Rubric r : rubrics) {
            sb.append("- ").append(r.getName()).append("（满分 ").append(r.getMaxScore()).append("）：")
                    .append(null2(r.getDescription())).append('\n');
        }
        sb.append("\n## 学生代码\n```java\n").append(null2(sub.getCode())).append("\n```\n");
        sb.append("\n请按 JSON 格式输出评分结果。");
        return sb.toString();
    }

    private AiGradeResult parseGradeResult(String raw, List<Rubric> rubrics) {
        String json = extractJson(raw);
        for (int attempt = 0; attempt < 2; attempt++) {
            try {
                JsonNode root = objectMapper.readTree(json);
                AiGradeResult result = new AiGradeResult();
                result.aiScore = root.path("aiScore").asInt(0);
                result.comment = root.path("comment").asText("");
                JsonNode scores = root.path("rubricScores");
                Map<Long, Integer> byId = new LinkedHashMap<>();
                for (Rubric r : rubrics) {
                    if (scores.has(r.getName())) {
                        byId.put(r.getId(), Math.min(scores.get(r.getName()).asInt(0), r.getMaxScore()));
                    }
                }
                if (result.aiScore == 0 && byId.isEmpty()) {
                    throw new IllegalStateException("空评分结果");
                }
                if (result.aiScore == 0) {
                    result.aiScore = byId.values().stream().mapToInt(Integer::intValue).sum();
                }
                result.rubricScores = byId;
                return result;
            } catch (Exception e) {
                if (attempt == 1) {
                    // 降级：纯文本评语
                    AiGradeResult fallback = new AiGradeResult();
                    fallback.aiScore = 0;
                    fallback.comment = "AI 初评解析失败（原文摘要）：" + shorten(raw);
                    fallback.rubricScores = new LinkedHashMap<>();
                    return fallback;
                }
                json = repairJson(json);
            }
        }
        throw new BizException("评分解析失败");
    }

    private String extractJson(String raw) {
        if (raw == null) {
            return "{}";
        }
        String t = raw.trim();
        t = t.replace("```json", "").replace("```", "").trim();
        int start = t.indexOf('{');
        int end = t.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return t.substring(start, end + 1);
        }
        return t;
    }

    private String repairJson(String json) {
        // 去尾逗号
        return json.replaceAll(",\\s*}", "}").replaceAll(",\\s*]", "]");
    }

    private String rubricName(List<Rubric> rubrics, Long id) {
        return rubrics.stream().filter(r -> r.getId().equals(id))
                .findFirst().map(Rubric::getName).orElse(String.valueOf(id));
    }

    private String shorten(String s) {
        return s != null && s.length() > 120 ? s.substring(0, 120) + "…" : s;
    }

    private String null2(String s) {
        return s != null ? s : "";
    }

    private static class AiGradeResult {
        int aiScore;
        String comment;
        Map<Long, Integer> rubricScores;
    }

    /** AgentPrompts 是 @Component，此处静态读取 grading 提示词 */
    private static class AgentPromptsHelper {
        private static final com.la.ai.AgentPrompts PROMPTS = new com.la.ai.AgentPrompts();
        static {
            PROMPTS.load();
        }
        static String loadGradingPrompt() {
            return PROMPTS.get(AgentPrompts.GRADING);
        }
    }
}
