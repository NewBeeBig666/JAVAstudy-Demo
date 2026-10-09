package com.la.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("knowledge_point")
public class KnowledgePoint {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 对外字符串编码，如 kp-collection / m1 */
    private String kpCode;

    private String name;

    private Long parentId;

    /** 1 一级（模块）/ 2 二级（知识点） */
    private Integer level;

    /** 教学大纲单元号 1-11 */
    private Integer unitNo;

    private String description;

    /** JSON 数组字符串 */
    private String resources;

    private Integer sortOrder;
}
