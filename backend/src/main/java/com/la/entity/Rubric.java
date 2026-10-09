package com.la.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("rubric")
public class Rubric {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long assignmentId;

    private String name;

    private Integer maxScore;

    private String description;

    private Integer sortOrder;
}