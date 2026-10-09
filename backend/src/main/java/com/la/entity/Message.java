package com.la.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("message")
public class Message {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long sessionId;

    /** SYS / USER / AGENT */
    private String sender;

    /** diagnosis / planning / tutor / code / grading / insight */
    private String agentType;

    private String content;

    /** diagnosis / path / exercise / result / code / codeCoach / report / feedback */
    private String cardType;

    /** JSON 对象字符串 */
    private String cardPayload;

    private LocalDateTime createdAt;
}
