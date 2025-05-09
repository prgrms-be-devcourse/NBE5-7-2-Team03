package com.team573.gongguri.global.exception;

import lombok.Getter;

@Getter
public class ErrorException extends RuntimeException {
    private final ErrorCode errorCode;

    public ErrorException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
