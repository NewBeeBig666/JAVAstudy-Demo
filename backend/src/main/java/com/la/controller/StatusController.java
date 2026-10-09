package com.la.controller;

import com.la.ai.LlmClient;
import com.la.config.DeepSeekProperties;
import com.la.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "系统状态")
@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
public class StatusController {

    private final LlmClient llmClient;
    private final DeepSeekProperties props;

    @Operation(summary = "AI 接入状态（前端据此展示接入模式）")
    @GetMapping
    public ApiResponse<Map<String, Object>> status() {
        return ApiResponse.ok(Map.of(
                "mock", llmClient.isMock(),
                "model", props.getModel(),
                "provider", llmClient.isMock() ? "mock" : "deepseek"));
    }
}
