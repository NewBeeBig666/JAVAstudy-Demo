package com.la.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wrong_book")
public class WrongBook {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long exerciseId;

    private Long kpId;

    private Integer wrongCount;

    private LocalDateTime lastWrongAt;
}
