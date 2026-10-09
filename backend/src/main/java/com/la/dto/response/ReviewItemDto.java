package com.la.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 复习队列项（形状对齐 MOCK.student.reviewQueue，附加 id/exerciseId）
 */
@Data
@AllArgsConstructor
public class ReviewItemDto {

    private Long id;
    private String kp;
    private String title;
    private String due;
    private String reason;
    private Long exerciseId;
}
