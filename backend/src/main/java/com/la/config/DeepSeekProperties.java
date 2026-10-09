package com.la.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * DeepSeek 配置。api-key 为空（或 force-mock=true）时自动降级为 Mock 客户端
 */
@Data
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekProperties {

    private String baseUrl = "https://api.deepseek.com";

    private String apiKey = "";

    private String model = "deepseek-chat";

    /** 连接超时（毫秒） */
    private long connectTimeout = 5000;

    /** 读超时（毫秒），流式对话需给足 */
    private long readTimeout = 90000;

    /** 批改/评分任务固定 Temperature=0 */
    private double gradeTemperature = 0.0;

    /** 强制使用 Mock 客户端（离线演示） */
    private boolean forceMock = false;

    public boolean mockMode() {
        return forceMock || apiKey == null || apiKey.isBlank();
    }
}
