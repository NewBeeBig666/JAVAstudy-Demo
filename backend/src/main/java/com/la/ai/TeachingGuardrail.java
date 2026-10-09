package com.la.ai;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 教学边界护栏：检测 LLM 回复中的「完整实现」代码块并替换为骨架。
 * 判定标准：代码块 ≥ 15 行 且 含方法签名与 return/输出语句 且 无 TODO/填空标记。
 */
@Component
public class TeachingGuardrail {

    private static final Pattern FENCE = Pattern.compile("```(\\w*)\\n(.*?)```", Pattern.DOTALL);
    /** 方法签名：返回类型 + 方法名 + ( ，如 "public int solve(" / "void run(" */
    private static final Pattern SIGNATURE = Pattern.compile("\\w+\\s+\\w+\\s*\\(");
    private static final Pattern RETURN_OR_PRINT = Pattern.compile("return\\s|System\\.out\\.print");

    public boolean isMockSafe() {
        return true;
    }

    /**
     * @return 处理后的文本；若有拦截，替换完整实现为骨架
     */
    public String sanitize(String text) {
        if (text == null) {
            return null;
        }
        Matcher m = FENCE.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String lang = m.group(1);
            String code = m.group(2);
            if (isCompleteImplementation(code)) {
                String skeleton = skeletonize(lang, code);
                m.appendReplacement(sb, Matcher.quoteReplacement(skeleton));
            } else {
                m.appendReplacement(sb, Matcher.quoteReplacement(m.group(0)));
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private boolean isCompleteImplementation(String code) {
        List<String> lines = new ArrayList<>(List.of(code.split("\n")));
        long codeLines = lines.stream().filter(l -> !l.trim().isEmpty() && !l.trim().startsWith("//")
                && !l.trim().startsWith("*") && !l.trim().startsWith("/*")).count();
        boolean hasTodo = code.contains("TODO") || code.contains("请你补全") || code.contains("// your code");
        return codeLines >= 15 && SIGNATURE.matcher(code).find()
                && RETURN_OR_PRINT.matcher(code).find() && !hasTodo;
    }

    private String skeletonize(String lang, String code) {
        StringBuilder sb = new StringBuilder("```").append(lang).append('\n');
        int kept = 0;
        for (String line : code.split("\n")) {
            String trimmed = line.trim();
            boolean isSignature = SIGNATURE.matcher(trimmed).find() && trimmed.endsWith("{");
            boolean isClassDecl = trimmed.startsWith("public class") || trimmed.startsWith("class");
            boolean isComment = trimmed.startsWith("//") || trimmed.startsWith("*") || trimmed.startsWith("/*");
            if (isClassDecl || isSignature || isComment) {
                sb.append(line).append('\n');
                kept++;
            }
        }
        if (kept == 0) {
            sb.append("// 结构骨架已保留，具体实现请自行完成\n");
        } else {
            sb.append("    // TODO: 请你补全核心逻辑（教学边界：不直接提供完整实现）\n");
        }
        sb.append("}```");
        return sb.toString();
    }
}
