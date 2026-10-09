package com.la.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("`class`")
public class ClassEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String grade;

    private Long teacherId;
}
