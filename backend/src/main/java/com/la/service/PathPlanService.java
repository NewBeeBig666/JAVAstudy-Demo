package com.la.service;

import com.la.dto.response.TraceChainResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 个性化学习路径推荐（纯算法，无 LLM）：
 * 依赖链溯源 → 定位根因 → 补漏序列（天然拓扑序：根在前）→ 生成 4 步路径
 */
@Service
@RequiredArgsConstructor
public class PathPlanService {

    public static class PlannedStep {
        public final String title;
        public final String description;
        public final int durationMin;
        /** 绑定的知识点编码（可空） */
        public final String bindKp;

        public PlannedStep(String title, String description, int durationMin, String bindKp) {
            this.title = title;
            this.description = description;
            this.durationMin = durationMin;
            this.bindKp = bindKp;
        }
    }

    /**
     * @param targetCode 目标知识点编码
     * @param chain      依赖链溯源结果（[目标, 前置1, ... 最弱根因方向]）
     */
    public List<PlannedStep> plan(String targetCode, String targetName, TraceChainResponse chain) {
        // 补漏序列：从根因到目标之前的节点，顺序为「根在前」的拓扑序
        List<String> remedial = new ArrayList<>();
        if (chain.getRootCause() != null) {
            List<String> codes = chain.getChain().stream().map(TraceChainResponse.ChainNode::getId).toList();
            int rootIdx = codes.indexOf(chain.getRootCause());
            for (int i = rootIdx; i >= 1; i--) { // 跳过 idx=0 的目标自身
                remedial.add(codes.get(i));
            }
        }

        TraceChainResponse.ChainNode root = chain.getChain().isEmpty() ? null
                : chain.getChain().get(chain.getChain().size() - 1);

        List<PlannedStep> steps = new ArrayList<>();
        steps.add(new PlannedStep(
                "诊断定位：" + targetName + "摸底",
                remedial.isEmpty()
                        ? "快速判断你卡在「概念理解」还是「动手运用」上。"
                        : "快速判断你卡在「目标知识点」还是前置薄弱上，依赖链已定位。",
                5, targetCode));

        if (!remedial.isEmpty()) {
            String first = remedial.get(0);
            String firstName = nodeName(chain, first);
            steps.add(new PlannedStep(
                    "概念精讲：" + firstName + "（根因补强）",
                    "你的根本薄弱点在「" + firstName + "」，先补这块地基，再回到「" + targetName + "」。",
                    15, first));
        } else {
            steps.add(new PlannedStep(
                    "概念精讲：" + targetName,
                    "围绕高频考点做渐进式讲解，配代码示例与反例。",
                    15, targetCode));
        }

        steps.add(new PlannedStep(
                "代码陪练：" + targetName,
                remedial.size() > 1
                        ? "给出测试用例，分三级提示引导补全逻辑（预计需先补 " + (remedial.size() - 1) + " 个前置点）。"
                        : "给出测试用例，分三级提示引导你补全核心逻辑，不直接给完整答案。",
                25, targetCode));

        steps.add(new PlannedStep(
                "巩固测验 + 错题归档",
                "变式题即时评测，错题自动进入间隔重复（SM-2）复习队列。",
                10, targetCode));

        return steps;
    }

    private String nodeName(TraceChainResponse chain, String code) {
        return chain.getChain().stream()
                .filter(n -> n.getId().equals(code))
                .findFirst().map(TraceChainResponse.ChainNode::getName).orElse(code);
    }
}
