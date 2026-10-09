package com.la.controller;

import com.la.security.CurrentUser;
import com.la.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE 流式对话端点
 * 事件协议：meta / delta / card / replace / done / error
 */
@Slf4j
@Tag(name = "智能体对话（SSE 流式）")
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "会话内流式对话（Accept: text/event-stream）")
    @PostMapping(value = "/{id}/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@PathVariable Long id, @RequestBody ChatRequest req) {
        SseEmitter emitter = new SseEmitter(120_000L);
        emitter.onCompletion(() -> log.debug("SSE 完成: session={}", id));
        emitter.onTimeout(emitter::complete);
        chatService.streamChat(CurrentUser.id(), id, req.getContent(), req.getAgentHint(), emitter);
        return emitter;
    }

    @Data
    public static class ChatRequest {
        @NotBlank(message = "消息不能为空")
        private String content;
        /** 快捷指令的显式 Agent 提示：tutor/code/diagnosis/planning */
        private String agentHint;
    }
}
