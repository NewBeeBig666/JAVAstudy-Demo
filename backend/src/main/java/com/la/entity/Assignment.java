package com.la.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("assignment")
public class Assignment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long teacherId;

    private Long classId;

    private String title;

    private String description;

    /** JSON 数组字符串：知识点编码 */
    private String kpIds;

    private LocalDateTime dueAt;

    /** DRAFT / ONGOING / CLOSED */
    private String status;

    private LocalDateTime createdAt;
}