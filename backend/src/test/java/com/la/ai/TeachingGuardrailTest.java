package com.la.ai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 教学边界护栏单元测试：完整实现代码被替换为骨架，含 TODO 的骨架放行
 */
class TeachingGuardrailTest {

    private final TeachingGuardrail guardrail = new TeachingGuardrail();

    @Test
    void completeImplementation_isSkeletonized() {
        StringBuilder code = new StringBuilder("public class Solution {\n");
        code.append("    public int solve(int[] nums) {\n");
        for (int i = 0; i < 18; i++) {
            code.append("        int x").append(i).append(" = ").append(i).append(";\n");
        }
        code.append("        return nums[0];\n    }\n}");
        String text = "参考实现：\n```java\n" + code + "\n```";

        String result = guardrail.sanitize(text);
        System.out.println("=== sanitized ===\n" + result);
        // 完整实现被替换：不再包含原实现体
        assertFalse(result.contains("return nums[0]"));
        // 保留类与方法签名骨架
        assertTrue(result.contains("public class Solution"));
        assertTrue(result.contains("public int solve"));
    }

    @Test
    void skeletonWithTodo_isAllowed() {
        String text = "```java\npublic class Solution {\n"
                + "    public int solve(int[] nums) {\n"
                + "        // TODO: 请你补全核心逻辑\n"
                + "        return 0;\n    }\n}\n```";
        assertEquals(text, guardrail.sanitize(text));
    }

    @Test
    void shortSnippet_isAllowed() {
        String text = "```java\nint a = 1;\nint b = 2;\nreturn a + b;\n```";
        assertEquals(text, guardrail.sanitize(text));
    }

    @Test
    void plainText_isUntouched() {
        String text = "这个问题可以从三个层面理解：是什么、为什么、怎么用。";
        assertEquals(text, guardrail.sanitize(text));
    }
}
