package com.d209.welight.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private final int status;
    private final String code;
    private final String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus().value();
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
    }

    // 커스텀 메시지를 받는 생성자 추가
    public ErrorResponse(ErrorCode errorCode, String customMessage) {
        this.status = errorCode.getStatus().value();
        this.code = errorCode.name();
        this.message = customMessage;
    }
}