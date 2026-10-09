package com.la.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("submission")
public class Submission {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long assignmentId;

    private Long studentId;

    private String code;

    /** MISSING / PENDING / AI_REVIEWED / GRADED */
    private String status;

    private Integer attempts;

    private Integer similarity;

    private Integer aiScore;

    private String aiComment;

    private String teacherComment;

    private Integer score;

    private LocalDateTime submittedAt;

    private LocalDateTime gradedAt;
}