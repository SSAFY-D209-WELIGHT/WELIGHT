package com.d209.welight.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "엔티티를 찾을 수 없습니다"),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 파라미터입니다"), 
    INVALID_STATE(HttpStatus.BAD_REQUEST, "잘못된 상태입니다"),
    NO_SEARCH_RESULT(HttpStatus.NOT_FOUND, "검색 결과가 없습니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),
    NO_FOUND_RESULT(HttpStatus.NOT_FOUND, "조회 결과가 없습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다"),
    INVALID_DISPLAY_DATA(HttpStatus.BAD_REQUEST, "필요한 정보가 부족합니다"),
    USER_CONFLICT(HttpStatus.CONFLICT, "충돌이 발생했습니다");

    private final HttpStatus status; // getStatus() 메서드를 위해 필드명 변경
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
