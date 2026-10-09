package com.la.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TraceChainResponse {

    private List<ChainNode> chain;
    /** 最弱根因知识点编码，可为 null */
    private String rootCause;
    private String conclusion;

    @Data
    @AllArgsConstructor
    public static class ChainNode {
        private String id;
        private String name;
        private int mastery;
        private String note;
    }
}
