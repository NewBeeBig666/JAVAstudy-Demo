package com.la.ai;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * OpenAI 兼容消息
 */
@Data
@AllArgsConstructor
public class ChatMessage {

    /** system / user / assistant */
    private String role;
    private String content;

    public static ChatMessage system(String content) {
        return new ChatMessage("system", content);
    }

    public static ChatMessage user(String content) {
        return new ChatMessage("user", content);
    }

    public static ChatMessage assistant(String content) {
        return new ChatMessage("assistant", content);
    }
}
