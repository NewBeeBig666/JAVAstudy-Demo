package com.la.exception;

import lombok.Getter;

/**
 * 业务异常：msg 直接返回给前端展示
 */
@Getter
public class BizException extends RuntimeException {

    private final int code;

    public BizException(String message) {
        this(400, message);
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }
}
