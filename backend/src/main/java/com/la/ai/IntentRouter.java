package com.la.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * 意图路由：快捷指令带显式 agentHint；自由输入按关键词规则路由。
 * 结构化动作（出练习题、复习队列）不走向 LLM，直接查库保证数据可靠。
 */
@Component
public class IntentRouter {

    public enum Action {
        /** 普通对话（交给 LLM 流式回答） */
        CHAT,
        /** 出一道练习题（查库出卡片） */
        EXERCISE,
        /** 今日复习队列（查库文本展示） */
        REVIEW
    }

    @Data
    @AllArgsConstructor
    public static class Intent {
        /** 目标 Agent：tutor / code / diagnosis / planning */
        private String agent;
        private Action action;
    }

    private static final Pattern EXERCISE = Pattern.compile("出题|变式|来一道|来个练习|练习一下|考考我");
    private static final Pattern ERROR_HELP = Pattern.compile("报错|异常|帮我看|帮我看看|错误|为什么错");
    private static final Pattern PATH = Pattern.compile("路径|规划|怎么学|学习计划");
    private static final Pattern WEAK = Pattern.compile("薄弱|该补|最应该|优先学");
    private static final Pattern REVIEW = Pattern.compile("复习|回顾");

    public Intent route(String text, String agentHint) {
        String t = text == null ? "" : text;

        // 快捷指令显式提示优先（但仍识别出题动作）
        if (EXERCISE.matcher(t).find()) {
            return new Intent(AgentPrompts.CODE, Action.EXERCISE);
        }
        if (REVIEW.matcher(t).find() && t.length() <= 30) {
            return new Intent(AgentPrompts.TUTOR, Action.REVIEW);
        }
        if (agentHint != null && !agentHint.isBlank()) {
            String hint = switch (agentHint.toLowerCase()) {
                case "code" -> AgentPrompts.CODE;
                case "diagnosis" -> AgentPrompts.DIAGNOSIS;
                case "planning" -> AgentPrompts.PLANNING;
                default -> AgentPrompts.TUTOR;
            };
            return new Intent(hint, Action.CHAT);
        }
        if (ERROR_HELP.matcher(t).find()) {
            return new Intent(AgentPrompts.CODE, Action.CHAT);
        }
        if (PATH.matcher(t).find()) {
            return new Intent(AgentPrompts.PLANNING, Action.CHAT);
        }
        if (WEAK.matcher(t).find()) {
            return new Intent(AgentPrompts.DIAGNOSIS, Action.CHAT);
        }
        return new Intent(AgentPrompts.TUTOR, Action.CHAT);
    }
}
