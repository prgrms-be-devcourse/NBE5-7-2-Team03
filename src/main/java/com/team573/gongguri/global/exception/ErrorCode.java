package com.team573.gongguri.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static com.team573.gongguri.global.exception.ErrorStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    NOT_FOUND_EXAMPLE(NOT_FOUND, "EXAMPLE-001", "존재하지 않는 예시입니다."),
  
    //GROUP_PURCHASE
    NOT_FOUND_GROUP_PURCHASE(NOT_FOUND, "POST-001", "해당 공동구매 게시글이 존재하지 않습니다."),
    CREATE_FAILED_GROUP_PURCHASE(CONFLICT, "POST-002", "공동구매 게시글 생성에 실패했습니다."),
    UPDATE_FAILED_GROUP_PURCHASE(CONFLICT, "POST-003", "공동구매 게시글 수정에 실패했습니다."),
    DELETE_FAILED_GROUP_PURCHASE(CONFLICT, "POST-004", "공동구매 게시글 삭제에 실패했습니다."),
    UNAUTHORIZED_GROUP_PURCHASE_ACCESS(FORBIDDEN, "POST-005", "해당 공동구매에 접근 권한이 없습니다."),

    // COMMON
    INVALID_REQUEST(BAD_REQUEST, "COMMON-001", "잘못된 요청입니다."),

    // AUTH
    FAILED_AUTHENTICATION(UNAUTHORIZED, "AUTH-01", "사용자 인증에 실패했습니다."),

    // MEMBER
    NOT_FOUND_MEMBER(NOT_FOUND, "MEMBER-001", "존재하지 않는 회원입니다."),

    // CHAT
    NOT_FOUND_ROOM(NOT_FOUND, "CHAT-001", "존재하지 않는 채팅방입니다."),
    NOT_PARTICIPATING(FORBIDDEN, "CHAT-002", "채팅방에 참여하고 있지 않습니다."),

    //UNIV
    NOT_FOUND_UNIV(NOT_FOUND, "UNIV-001", "해당 대학교 정보가 존재하지 않습니다.");
  

    private final ErrorStatus errorStatus;
    private final String code;
    private final String message;


}
