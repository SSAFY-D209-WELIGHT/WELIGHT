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
    USER_CONFLICT(HttpStatus.CONFLICT, "충돌이 발생했습니다"),

    //cheerroom
    DUPLICATE_CHEERROOM_NAME(HttpStatus.BAD_REQUEST, "이미 존재하는 응원방 이름입니다"),
    INVALID_LATITUDE(HttpStatus.BAD_REQUEST, "위도는 -90도에서 90도 사이의 값이어야 합니다"),
    INVALID_LONGITUDE(HttpStatus.BAD_REQUEST, "경도는 -180도에서 180도 사이의 값이어야 합니다"),
    CHEERROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 응원방입니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),
    NOT_PARTICIPATED(HttpStatus.NOT_FOUND, "해당 응원방에 참여하지 않은 사용자입니다"),
    NOT_CHEERROOM_LEADER(HttpStatus.FORBIDDEN, "방장만 이 작업을 수행할 수 있습니다"),
    ;

    private final HttpStatus status; // getStatus() 메서드를 위해 필드명 변경
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
