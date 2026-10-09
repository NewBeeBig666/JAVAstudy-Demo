package com.la.ai;

import com.la.entity.KnowledgePoint;
import com.la.entity.Message;
import com.la.service.KnowledgeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.la.entity.WrongBook;
import com.la.mapper.MessageMapper;
import com.la.mapper.WrongBookMapper;
import com.la.mapper.ExerciseMapper;
import com.la.entity.Exercise;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 上下文注入：组装 system 提示词（Agent 模板 + 知识点上下文）与历史消息
 */
@Component
@RequiredArgsConstructor
public class ContextInjector {

    private final AgentPrompts agentPrompts;
    private final KnowledgeService knowledgeService;
    private final MessageMapper messageMapper;
    private final WrongBookMapper wrongBookMapper;
    private final ExerciseMapper exerciseMapper;

    /**
     * 构建 LLM 消息序列：system（Agent模板+知识点上下文） + 最近历史 + 无（当前输入由调用方追加）
     */
    public List<ChatMessage> build(Long userId, Long sessionId, KnowledgePoint kp, String agent) {
        StringBuilder system = new StringBuilder(agentPrompts.get(agent));

        if (kp != null) {
            Map<Long, Integer> masteryMap = knowledgeService.masteryMap(userId);
            Map<Long, List<Long>> depMap = knowledgeService.depMap();
            system.append("\n\n## 当前学习上下文\n");
            system.append("- 知识点：").append(kp.getName())
                    .append("（模块：").append(moduleName(kp)).append("）\n");
            system.append("- 学生当前掌握度：").append(masteryMap.getOrDefault(kp.getId(), 0)).append("/100\n");
            if (kp.getDescription() != null) {
                system.append("- 知识点说明：").append(kp.getDescription()).append('\n');
            }
            List<Long> deps = depMap.getOrDefault(kp.getId(), List.of());
            if (!deps.isEmpty()) {
                String depText = deps.stream()
                        .map(id -> {
                            com.la.entity.KnowledgePoint d = knowledgeService.byId(id);
                            return d.getName() + "(" + masteryMap.getOrDefault(id, 0) + "分)";
                        })
                        .collect(Collectors.joining("、"));
                system.append("- 前置依赖及掌握度：").append(depText).append('\n');
            }
            // 最近错题
            List<WrongBook> wrongs = wrongBookMapper.selectList(new LambdaQueryWrapper<WrongBook>()
                    .eq(WrongBook::getUserId, userId).eq(WrongBook::getKpId, kp.getId())
                    .orderByDesc(WrongBook::getLastWrongAt).last("LIMIT 2"));
            if (!wrongs.isEmpty()) {
                String wrongText = wrongs.stream().map(w -> {
                    Exercise ex = exerciseMapper.selectById(w.getExerciseId());
                    String stem = ex != null ? ex.getStem().replaceAll("\\s+", " ") : "";
                    return stem.length() > 30 ? stem.substring(0, 30) + "…" : stem;
                }).collect(Collectors.joining("；"));
                system.append("- 该知识点最近错题：").append(wrongText).append('\n');
            }
            system.append("- 教学大纲单元：第 ").append(kp.getUnitNo() != null ? kp.getUnitNo() : "?").append(" 单元\n");
        }

        // 历史消息（最近 10 条 USER/AGENT）
        List<Message> history = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getSessionId, sessionId)
                .in(Message::getSender, List.of("USER", "AGENT"))
                .orderByDesc(Message::getCreatedAt)
                .last("LIMIT 10"));

        java.util.Collections.reverse(history);

        List<ChatMessage> result = new java.util.ArrayList<>();
        result.add(ChatMessage.system(system.toString()));
        for (Message m : history) {
            String text = m.getContent();
            if (m.getCardType() != null) {
                text = (text == null ? "" : text) + " [卡片:" + m.getCardType() + "]";
            }
            if ("USER".equals(m.getSender())) {
                result.add(ChatMessage.user(text));
            } else {
                result.add(ChatMessage.assistant(text));
            }
        }
        return result;
    }

    private String moduleName(KnowledgePoint kp) {
        if (kp.getParentId() == null) {
            return kp.getName();
        }
        com.la.entity.KnowledgePoint parent = knowledgeService.byId(kp.getParentId());
        return parent != null ? parent.getName() : "-";
    }
}
