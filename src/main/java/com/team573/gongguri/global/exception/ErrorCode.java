package com.team573.gongguri.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static com.team573.gongguri.global.exception.ErrorStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    NOT_FOUND_EXAMPLE(NOT_FOUND, "EXAMPLE-001", "존재하지 않는 예시입니다."),
    NOT_FOUND_GROUP_PURCHASE(NOT_FOUND, "POST-001", "해당 공동구매 게시글이 존재하지 않습니다."),
    CREATE_FAILED_GROUP_PURCHASE(CONFLICT, "POST-002", "공동구매 게시글 생성에 실패했습니다."),
    UPDATE_FAILED_GROUP_PURCHASE(CONFLICT, "POST-003", "공동구매 게시글 수정에 실패했습니다."),
    DELETE_FAILED_GROUP_PURCHASE(CONFLICT, "POST-004", "공동구매 게시글 삭제에 실패했습니다."),
    UNAUTHORIZED_GROUP_PURCHASE_ACCESS(FORBIDDEN, "POST-005", "해당 공동구매에 접근 권한이 없습니다."),

    NOT_FOUND_MEMBER(NOT_FOUND, "MEMBER-001", "해당 회원이 존재하지 않습니다."),
    NOT_FOUND_UNIV(NOT_FOUND, "UNIV-001", "해당 대학교 정보가 존재하지 않습니다."),
    NOT_FOUND_CHATROOM(NOT_FOUND, "CHAT-001", "채팅방 정보를 찾을 수 없습니다.");

    private final ErrorStatus errorStatus;
    private final String code;
    private final String message;

}
