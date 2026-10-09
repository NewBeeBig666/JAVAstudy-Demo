package com.la.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("warning")
public class Warning {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long classId;

    /** NULL = 班级共性预警 */
    private Long studentId;

    /** HIGH / MID */
    private String level;

    private String type;

    private String description;

    private Boolean handled;

    private LocalDateTime handledAt;

    private LocalDateTime createdAt;
}