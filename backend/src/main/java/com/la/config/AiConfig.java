package com.la.config;

import com.la.ai.DeepSeekClient;
import com.la.ai.LlmClient;
import com.la.ai.MockDeepSeekClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * AI 客户端装配：有 Key 用 DeepSeek，无 Key 自动降级 Mock
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(DeepSeekProperties.class)
public class AiConfig {

    @Bean
    public LlmClient llmClient(DeepSeekProperties props) {
        if (props.mockMode()) {
            log.warn("DeepSeek 未配置 API Key（或 force-mock=true），使用 Mock 客户端运行");
            return new MockDeepSeekClient();
        }
        log.info("DeepSeek 客户端已启用，model={}", props.getModel());
        return new DeepSeekClient(props);
    }

    /**
     * 批量 AI 初评 / 异步任务线程池
     */
    @Bean("gradingExecutor")
    public ThreadPoolTaskExecutor gradingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("ai-grade-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
