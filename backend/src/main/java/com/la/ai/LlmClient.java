package com.la.ai;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 * LLM 客户端抽象（DeepSeek / Mock 双实现，可扩展其他厂商）
 */
public interface LlmClient {

    /**
     * 非流式对话（批改、学情报告等）。失败抛 RuntimeException。
     */
    String chat(List<ChatMessage> messages, double temperature);

    /**
     * 流式对话，逐段返回增量文本
     */
    Flux<String> chatStream(List<ChatMessage> messages, double temperature);

    boolean isMock();
}
