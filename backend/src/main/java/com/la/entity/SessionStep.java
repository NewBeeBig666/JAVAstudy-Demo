package com.la.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("session_step")
public class SessionStep {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long sessionId;

    private Integer sortOrder;

    private String title;

    private String description;

    private Integer durationMin;

    /** PENDING / ACTIVE / DONE */
    private String status;
}
