package com.la.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("rubric_score")
public class RubricScore {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long submissionId;

    private Long rubricId;

    private Integer score;
}