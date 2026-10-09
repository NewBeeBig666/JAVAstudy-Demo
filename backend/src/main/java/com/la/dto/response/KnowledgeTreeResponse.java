package com.la.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 知识点树（字段形状对齐前端 MOCK.knowledgePoints / MOCK.modules）
 */
@Data
@AllArgsConstructor
public class KnowledgeTreeResponse {

    private List<ModuleDto> modules;
    private List<KpDto> knowledgePoints;

    @Data
    @AllArgsConstructor
    public static class ModuleDto {
        private String id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KpDto {
        private String id;
        private String name;
        private String module;
        private int mastery;
        private List<String> deps;
        private String desc;
        private List<String> resources;
    }
}
