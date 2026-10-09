package com.la.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.la.config.DeepSeekProperties;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * DeepSeek 客户端（OpenAI 兼容 /chat/completions）
 *
 * 安全与可观测性设计：
 * - API Key 仅通过 Authorization 头发送至配置的 base-url，不写入任何日志
 * - 每次调用记录：模型 / 消息数 / 耗时 / Token 用量 / 错误状态码
 * - deepseek-flash 为混合推理模型：流式 delta 先输出 reasoning_content（思维链）再输出 content，
 *   本客户端只向业务层转发 content，思维链不外泄给前端
 */
@Slf4j
public class DeepSeekClient implements LlmClient {

    private final WebClient webClient;
    private final DeepSeekProperties props;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DeepSeekClient(DeepSeekProperties props) {
        this.props = props;
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) props.getConnectTimeout())
                .responseTimeout(Duration.ofMillis(props.getReadTimeout()));
        this.webClient = WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + props.getApiKey())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        log.info("DeepSeek 客户端初始化完成: baseUrl={}, model={}, connectTimeout={}ms, readTimeout={}ms",
                props.getBaseUrl(), props.getModel(), props.getConnectTimeout(), props.getReadTimeout());
    }

    @Override
    public String chat(List<ChatMessage> messages, double temperature) {
        long start = System.currentTimeMillis();
        Map<String, Object> body = requestBody(messages, temperature, false);
        log.info("[DeepSeek] 非流式调用开始: model={}, messages={}, temperature={}",
                props.getModel(), messages.size(), temperature);
        try {
            String response = webClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    // 429/5xx 指数退避重试 2 次
                    .retryWhen(Retry.backoff(2, Duration.ofSeconds(1))
                            .filter(this::retryable)
                            .doBeforeRetry(s -> log.warn("[DeepSeek] 调用失败，第 {} 次重试: {}",
                                    s.totalRetries() + 1, s.failure().getMessage())))
                    .block();
            JsonNode root = objectMapper.readTree(response);
            String content = root.path("choices").path(0).path("message").path("content").asText("");
            JsonNode usage = root.path("usage");
            log.info("[DeepSeek] 非流式调用完成: model={}, 耗时={}ms, promptTokens={}, completionTokens={}, totalTokens={}",
                    props.getModel(), System.currentTimeMillis() - start,
                    usage.path("prompt_tokens").asInt(-1),
                    usage.path("completion_tokens").asInt(-1),
                    usage.path("total_tokens").asInt(-1));
            if (content.isBlank()) {
                log.warn("[DeepSeek] 响应 content 为空: finishReason={}",
                        root.path("choices").path(0).path("finish_reason").asText("?"));
            }
            return content;
        } catch (Exception e) {
            log.error("[DeepSeek] 非流式调用失败: model={}, 耗时={}ms, 错误={}",
                    props.getModel(), System.currentTimeMillis() - start, e.getMessage());
            throw new IllegalStateException("DeepSeek 调用失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Flux<String> chatStream(List<ChatMessage> messages, double temperature) {
        long start = System.currentTimeMillis();
        Map<String, Object> body = requestBody(messages, temperature, true);
        // 请求最终块的 usage 统计
        body.put("stream_options", Map.of("include_usage", true));
        log.info("[DeepSeek] 流式调用开始: model={}, messages={}, temperature={}",
                props.getModel(), messages.size(), temperature);

        AtomicReference<JsonNode> usageRef = new AtomicReference<>();
        AtomicReference<Integer> reasoningChunks = new AtomicReference<>(0);

        return webClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {
                })
                .mapNotNull(ServerSentEvent::data)
                .filter(data -> data != null && !"[DONE]".equals(data.trim()))
                .mapNotNull(data -> {
                    try {
                        JsonNode root = objectMapper.readTree(data);
                        if (root.has("usage") && !root.get("usage").isNull()) {
                            usageRef.set(root.get("usage"));
                        }
                        JsonNode delta = root.path("choices").path(0).path("delta");
                        // 混合推理模型：思维链（reasoning_content）只计数不转发，仅输出正文 content
                        if (!delta.path("reasoning_content").asText("").isEmpty()) {
                            reasoningChunks.updateAndGet(n -> n + 1);
                            return "";
                        }
                        return delta.path("content").asText("");
                    } catch (Exception e) {
                        return "";
                    }
                })
                .filter(s -> !s.isEmpty())
                .doOnComplete(() -> {
                    JsonNode usage = usageRef.get();
                    log.info("[DeepSeek] 流式调用完成: model={}, 耗时={}ms, 思维链块={}, totalTokens={}",
                            props.getModel(), System.currentTimeMillis() - start, reasoningChunks.get(),
                            usage != null ? usage.path("total_tokens").asInt(-1) : "n/a");
                })
                .doOnError(e -> log.error("[DeepSeek] 流式调用失败: model={}, 耗时={}ms, 错误={}",
                        props.getModel(), System.currentTimeMillis() - start, e.getMessage()));
    }

    private Map<String, Object> requestBody(List<ChatMessage> messages, double temperature, boolean stream) {
        List<Map<String, String>> msgs = messages.stream()
                .map(m -> Map.of("role", m.getRole(), "content", m.getContent()))
                .toList();
        Map<String, Object> body = new HashMap<>();
        body.put("model", props.getModel());
        body.put("messages", msgs);
        body.put("stream", stream);
        body.put("temperature", temperature);
        return body;
    }

    private boolean retryable(Throwable t) {
        String name = t.getClass().getSimpleName();
        return name.contains("ServiceUnavailable") || name.contains("TooManyRequests")
                || name.contains("HttpServerErrorException") || name.contains("ServerError");
    }

    @Override
    public boolean isMock() {
        return false;
    }
}
