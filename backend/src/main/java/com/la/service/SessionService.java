package com.la.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.la.dto.response.SessionDto;
import com.la.dto.response.TraceChainResponse;
import com.la.entity.*;
import com.la.exception.BizException;
import com.la.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionMapper sessionMapper;
    private final SessionStepMapper stepMapper;
    private final MessageMapper messageMapper;
    private final KnowledgePointMapper kpMapper;
    private final KnowledgeService knowledgeService;
    private final PathPlanService pathPlanService;
    private final ExerciseMapper exerciseMapper;
    private final ExerciseService exerciseService;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("MM-dd HH:mm");
    private static final DateTimeFormatter TM = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * 新建学习会话：依赖链溯源诊断 + 路径规划 + 首批消息（sys / 诊断Agent / 规划Agent）
     */
    @Transactional
    public SessionDto create(Long userId, String kpCode) {
        KnowledgePoint kp = kpMapper.selectOne(new LambdaQueryWrapper<KnowledgePoint>()
                .eq(KnowledgePoint::getKpCode, kpCode));
        if (kp == null) {
            throw new BizException("知识点不存在: " + kpCode);
        }

        TraceChainResponse chain = knowledgeService.traceChain(kpCode, userId);
        List<PathPlanService.PlannedStep> plan = pathPlanService.plan(kpCode, kp.getName(), chain);

        SessionEntity session = new SessionEntity();
        session.setUserId(userId);
        session.setTitle(kp.getName() + "：个性化攻克路径");
        session.setKpId(kp.getId());
        session.setStatus("ACTIVE");
        session.setStepIndex(0);
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        sessionMapper.insert(session);

        List<SessionStep> steps = new ArrayList<>();
        for (int i = 0; i < plan.size(); i++) {
            PathPlanService.PlannedStep ps = plan.get(i);
            SessionStep st = new SessionStep();
            st.setSessionId(session.getId());
            st.setSortOrder(i + 1);
            st.setTitle(ps.title);
            st.setDescription(ps.description);
            st.setDurationMin(ps.durationMin);
            st.setStatus(i == 0 ? "ACTIVE" : "PENDING");
            stepMapper.insert(st);
            steps.add(st);
        }

        int totalMin = plan.stream().mapToInt(p -> p.durationMin).sum();

        // sys 消息
        insertMessage(session.getId(), "SYS", null, "会话已创建 · 已载入你的知识图谱快照（"
                + countKps() + " 个知识点）", null, null);

        // 诊断 Agent 消息（规则化生成，含 diagnosis 卡片）
        StringBuilder diagText = new StringBuilder("我先调了你的历史练习记录和错题本，做了次快速诊断：\n\n");
        diagText.append("你的「").append(kp.getName()).append("」掌握度为 **")
                .append(chain.getChain().get(0).getMastery()).append(" 分**");
        if (chain.getRootCause() != null) {
            TraceChainResponse.ChainNode root = chain.getChain().stream()
                    .filter(n -> n.getId().equals(chain.getRootCause())).findFirst().orElse(null);
            diagText.append("，但更关键的是——**根源不在目标知识点本身**。\n\n顺着依赖链往上追：");
            StringJoiner joiner = new StringJoiner(" ← ");
            for (TraceChainResponse.ChainNode n : chain.getChain()) {
                joiner.add(n.getName() + "（" + n.getMastery() + " 分）");
            }
            diagText.append(joiner).append("。\n\n你真正缺的是「")
                    .append(root != null ? root.getName() : "前置基础")
                    .append("」这块地基，直接刷目标知识点的题目，收益很低。");
        } else {
            diagText.append("，前置链整体达标，可以直接攻克。");
        }

        List<Map<String, Object>> items = new ArrayList<>();
        for (int i = 0; i < chain.getChain().size(); i++) {
            TraceChainResponse.ChainNode n = chain.getChain().get(i);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("kp", n.getId());
            item.put("mastery", n.getMastery());
            item.put("note", n.getNote());
            items.add(item);
        }
        Map<String, Object> diagCard = new LinkedHashMap<>();
        diagCard.put("type", "diagnosis");
        diagCard.put("items", items);
        diagCard.put("conclusion", chain.getConclusion());
        insertMessage(session.getId(), "AGENT", "diagnosis", diagText.toString(),
                "diagnosis", diagCard);

        // 规划 Agent 消息（path 卡片）
        String planText = "按诊断结果，我给你排了一条 " + totalMin + " 分钟的路径，从第 1 步开始。"
                + "每一步都可以点开看详情，右侧上下文区会同步当前知识点摘要。";
        Map<String, Object> pathCard = new LinkedHashMap<>();
        pathCard.put("type", "path");
        pathCard.put("kp", kpCode);
        insertMessage(session.getId(), "AGENT", "planning", planText, "path", pathCard);

        return detail(userId, session.getId());
    }

    public List<SessionDto> list(Long userId) {
        List<SessionEntity> sessions = sessionMapper.selectList(new LambdaQueryWrapper<SessionEntity>()
                .eq(SessionEntity::getUserId, userId)
                .orderByDesc(SessionEntity::getUpdatedAt));
        return sessions.stream().map(s -> {
            List<SessionStep> steps = stepMapper.selectList(new LambdaQueryWrapper<SessionStep>()
                    .eq(SessionStep::getSessionId, s.getId())
                    .orderByAsc(SessionStep::getSortOrder));
            KnowledgePoint kp = kpMapper.selectById(s.getKpId());
            return toDto(s, steps, List.of(), kp, true);
        }).collect(Collectors.toList());
    }

    public SessionDto detail(Long userId, Long sessionId) {
        SessionEntity s = ownedSession(userId, sessionId);
        List<SessionStep> steps = stepMapper.selectList(new LambdaQueryWrapper<SessionStep>()
                .eq(SessionStep::getSessionId, sessionId).orderByAsc(SessionStep::getSortOrder));
        List<Message> messages = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getSessionId, sessionId).orderByAsc(Message::getCreatedAt));
        KnowledgePoint kp = kpMapper.selectById(s.getKpId());
        return toDto(s, steps, messages, kp, false);
    }

    /**
     * 开始某一步骤；代码陪练步骤会自动生成练习卡片消息
     */
    @Transactional
    public SessionDto startStep(Long userId, Long sessionId, int stepNo) {
        SessionEntity s = ownedSession(userId, sessionId);
        List<SessionStep> steps = stepMapper.selectList(new LambdaQueryWrapper<SessionStep>()
                .eq(SessionStep::getSessionId, sessionId).orderByAsc(SessionStep::getSortOrder));
        for (int i = 0; i < steps.size(); i++) {
            SessionStep st = steps.get(i);
            if (i + 1 < stepNo) {
                st.setStatus("DONE");
            } else if (i + 1 == stepNo) {
                if (!"DONE".equals(st.getStatus())) {
                    st.setStatus("ACTIVE");
                }
            }
            stepMapper.updateById(st);
        }
        s.setStepIndex(stepNo - 1);
        s.setUpdatedAt(LocalDateTime.now());
        sessionMapper.updateById(s);

        SessionStep target = steps.stream()
                .filter(st -> st.getSortOrder() == stepNo).findFirst().orElse(null);
        if (target != null && target.getTitle().startsWith("代码陪练") && !exerciseCardExists(sessionId)) {
            List<Exercise> exList = exerciseMapper.selectList(new LambdaQueryWrapper<Exercise>()
                    .eq(Exercise::getKpId, s.getKpId()));
            if (!exList.isEmpty()) {
                Exercise ex = exList.get(new Random().nextInt(exList.size()));
                Map<String, Object> card = new LinkedHashMap<>();
                card.put("type", "exercise");
                card.put("exId", ex.getId());
                card.put("exercise", exerciseService.exerciseMap(ex));
                card.put("state", "answering");
                card.put("hintLevel", 0);
                card.put("selected", null);
                insertMessage(sessionId, "AGENT", "code",
                        "这道是代码陪练题。按规则我**不会直接给你完整实现**，先读题干，自己作答；卡住了再逐级要提示（提示使用次数教师端可见）。",
                        "exercise", card);
            }
        }
        return detail(userId, sessionId);
    }

    /**
     * 完成某一步骤（练习提交正确后由前端调用）
     */
    @Transactional
    public SessionDto completeStep(Long userId, Long sessionId, int stepNo) {
        SessionEntity s = ownedSession(userId, sessionId);
        List<SessionStep> steps = stepMapper.selectList(new LambdaQueryWrapper<SessionStep>()
                .eq(SessionStep::getSessionId, sessionId).orderByAsc(SessionStep::getSortOrder));
        for (int i = 0; i < steps.size(); i++) {
            if (i + 1 == stepNo) {
                steps.get(i).setStatus("DONE");
                stepMapper.updateById(steps.get(i));
            }
        }
        boolean allDone = steps.stream().allMatch(st -> "DONE".equals(st.getStatus()));
        if (allDone) {
            s.setStatus("DONE");
            insertMessage(sessionId, "SYS", null, "会话已完成 · 学习报告已同步至画像与报告", null, null);
        } else {
            // 自动推进下一步
            int next = steps.stream().filter(st -> "PENDING".equals(st.getStatus()))
                    .mapToInt(SessionStep::getSortOrder).min().orElse(stepNo);
            startStep(userId, sessionId, next);
            return detail(userId, sessionId);
        }
        s.setUpdatedAt(LocalDateTime.now());
        sessionMapper.updateById(s);
        return detail(userId, sessionId);
    }

    /**
     * 重新规划路径
     */
    @Transactional
    public SessionDto replan(Long userId, Long sessionId) {
        SessionEntity s = ownedSession(userId, sessionId);
        KnowledgePoint kp = kpMapper.selectById(s.getKpId());
        TraceChainResponse chain = knowledgeService.traceChain(kp.getKpCode(), userId);
        List<PathPlanService.PlannedStep> plan = pathPlanService.plan(kp.getKpCode(), kp.getName(), chain);

        stepMapper.delete(new LambdaQueryWrapper<SessionStep>().eq(SessionStep::getSessionId, sessionId));
        for (int i = 0; i < plan.size(); i++) {
            PathPlanService.PlannedStep ps = plan.get(i);
            SessionStep st = new SessionStep();
            st.setSessionId(sessionId);
            st.setSortOrder(i + 1);
            st.setTitle(ps.title);
            st.setDescription(ps.description);
            st.setDurationMin(ps.durationMin);
            st.setStatus(i == 0 ? "ACTIVE" : "PENDING");
            stepMapper.insert(st);
        }
        s.setStatus("ACTIVE");
        s.setStepIndex(0);
        s.setUpdatedAt(LocalDateTime.now());
        sessionMapper.updateById(s);

        int totalMin = plan.stream().mapToInt(p -> p.durationMin).sum();
        insertMessage(sessionId, "AGENT", "planning",
                "已根据你的最新掌握度重新规划路径，共 " + totalMin + " 分钟，从头开始推进。",
                "path", Map.of("type", "path", "kp", kp.getKpCode()));
        return detail(userId, sessionId);
    }

    /**
     * 校验会话归属并返回
     */
    public SessionEntity ownedSession(Long userId, Long sessionId) {
        SessionEntity s = sessionMapper.selectById(sessionId);
        if (s == null || !s.getUserId().equals(userId)) {
            throw new BizException("会话不存在");
        }
        return s;
    }

    public void touch(Long sessionId) {
        SessionEntity s = sessionMapper.selectById(sessionId);
        if (s != null) {
            s.setUpdatedAt(LocalDateTime.now());
            sessionMapper.updateById(s);
        }
    }

    public Message insertMessage(Long sessionId, String sender, String agentType, String content,
                                 String cardType, Map<String, Object> cardPayload) {
        Message m = new Message();
        m.setSessionId(sessionId);
        m.setSender(sender);
        m.setAgentType(agentType);
        m.setContent(content);
        m.setCardType(cardType);
        m.setCardPayload(cardPayload != null ? toJson(cardPayload) : null);
        m.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(m);
        return m;
    }

    /**
     * 更新消息内容（流式对话完成后回填）
     */
    public void updateMessage(Message m) {
        messageMapper.updateById(m);
    }

    private SessionDto toDto(SessionEntity s, List<SessionStep> steps, List<Message> messages,
                             KnowledgePoint kp, boolean omitMessages) {
        List<SessionDto.StepDto> stepDtos = steps.stream().map(st -> new SessionDto.StepDto(
                st.getTitle(), st.getDescription(), st.getDurationMin() + " 分钟",
                st.getStatus().toLowerCase())).toList();

        List<SessionDto.MessageDto> msgDtos = omitMessages ? List.of() : messages.stream()
                .map(m -> new SessionDto.MessageDto(
                        String.valueOf(m.getId()),
                        m.getSender().toLowerCase(),
                        m.getAgentType(),
                        m.getCreatedAt() != null ? m.getCreatedAt().format(TM) : null,
                        m.getContent(),
                        parseCard(m.getCardType(), m.getCardPayload())))
                .toList();

        return new SessionDto(String.valueOf(s.getId()), s.getTitle(),
                kp != null ? kp.getKpCode() : null,
                s.getStatus().toLowerCase(),
                s.getCreatedAt() != null ? s.getCreatedAt().format(DT) : null,
                s.getUpdatedAt() != null ? s.getUpdatedAt().format(DT) : null,
                s.getStepIndex(), stepDtos, msgDtos);
    }

    private Map<String, Object> parseCard(String cardType, String payload) {
        if (cardType == null || payload == null) {
            return null;
        }
        try {
            return objectMapper.readValue(payload,
                    objectMapper.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, Object.class));
        } catch (Exception e) {
            return null;
        }
    }

    private String toJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            return "{}";
        }
    }

    private Long pickExercise(Long kpId) {
        List<Exercise> list = exerciseMapper.selectList(new LambdaQueryWrapper<Exercise>()
                .eq(Exercise::getKpId, kpId));
        if (list.isEmpty()) {
            return null;
        }
        return list.get(new Random().nextInt(list.size())).getId();
    }

    private boolean exerciseCardExists(Long sessionId) {
        return messageMapper.selectCount(new LambdaQueryWrapper<Message>()
                .eq(Message::getSessionId, sessionId)
                .eq(Message::getCardType, "exercise")) > 0;
    }

    private long countKps() {
        return kpMapper.selectCount(new LambdaQueryWrapper<KnowledgePoint>()
                .eq(KnowledgePoint::getLevel, 2));
    }
}
