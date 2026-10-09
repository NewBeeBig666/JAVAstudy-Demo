package com.la.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("kp_dependency")
public class KpDependency {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long kpId;

    private Long depKpId;

    /** PRE / POST */
    private String depType;
}
