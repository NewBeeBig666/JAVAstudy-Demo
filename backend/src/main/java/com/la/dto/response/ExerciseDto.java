package com.la.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 练习题 DTO（不包含 answer / analysis / hints，防泄露）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {

    private Long id;
    private String kp;
    private String type;
    private String difficulty;
    private String stem;
    private String code;
    private List<String> options;
    private Integer masteryDelta;
}
