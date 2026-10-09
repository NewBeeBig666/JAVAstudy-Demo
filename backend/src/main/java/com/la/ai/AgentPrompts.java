package com.la.ai;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 多 Agent 系统提示词（resources/prompts/*.md）
 */
@Slf4j
@Component
public class AgentPrompts {

    /** 会话对话类 Agent */
    public static final String TUTOR = "tutor";
    public static final String CODE = "code";
    public static final String DIAGNOSIS = "diagnosis";
    public static final String PLANNING = "planning";
    public static final String GRADING = "grading";
    public static final String INSIGHT = "insight";

    private final Map<String, String> prompts = new HashMap<>();

    @PostConstruct
    public void load() {
        Map<String, String> files = Map.of(
                TUTOR, "prompts/tutor.md",
                CODE, "prompts/code_coach.md",
                DIAGNOSIS, "prompts/diagnosis.md",
                PLANNING, "prompts/planning.md",
                GRADING, "prompts/grading.md",
                INSIGHT, "prompts/insight.md");
        files.forEach((agent, path) -> {
            try {
                String content = new String(new ClassPathResource(path).getInputStream().readAllBytes(),
                        StandardCharsets.UTF_8);
                prompts.put(agent, content.trim());
            } catch (Exception e) {
                log.error("加载 Agent 提示词失败: {}", path, e);
            }
        });
    }

    public String get(String agent) {
        return prompts.getOrDefault(agent, prompts.get(TUTOR));
    }
}
