package com.la.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("review_schedule")
public class ReviewSchedule {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long exerciseId;

    private Long kpId;

    private String title;

    /** SM-2 难度因子 */
    private BigDecimal easeFactor;

    private Integer intervalDays;

    private Integer repetitions;

    private LocalDate nextReviewDate;

    private LocalDate lastReviewDate;

    /** WRONG / WEAK */
    private String source;

    /** PENDING / DONE */
    private String status;
}
