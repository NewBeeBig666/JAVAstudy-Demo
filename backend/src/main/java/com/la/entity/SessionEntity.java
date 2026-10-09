package com.la.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("`session`")
public class SessionEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private Long kpId;

    /** ACTIVE / DONE */
    private String status;

    private Integer stepIndex;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
