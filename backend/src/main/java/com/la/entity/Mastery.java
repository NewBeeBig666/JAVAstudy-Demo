package com.la.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mastery")
public class Mastery {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long kpId;

    /** 0-100 */
    private Integer masteryValue;

    private LocalDateTime updatedAt;
}
