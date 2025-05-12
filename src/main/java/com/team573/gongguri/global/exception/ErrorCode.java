package com.team573.gongguri.global.exception;

import static com.team573.gongguri.global.exception.ErrorStatus.NOT_FOUND;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    NOT_FOUND_EXAMPLE(NOT_FOUND, "EXAMPLE-001", "존재하지 않는 예시입니다."),
    NOT_FOUND_GROUP_PURCHASE(NOT_FOUND, "POST-001", "해당 공동구매 게시글이 존재하지 않습니다.");


    private final ErrorStatus errorStatus;
    private final String code;
    private final String message;
}
