package com.la.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("exercise")
public class Exercise {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long kpId;

    /** CHOICE / CODE */
    private String type;

    /** 简单 / 中等 / 困难 */
    private String difficulty;

    private String stem;

    private String code;

    /** JSON 数组字符串 */
    private String options;

    private Integer answer;

    private String analysis;

    /** JSON 数组字符串：三级渐进提示 */
    private String hints;

    private Integer masteryDelta;
}
