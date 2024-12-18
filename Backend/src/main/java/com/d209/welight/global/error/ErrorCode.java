package com.d209.welight.global.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String name();
    HttpStatus getStatus();
    String getMessage();
}
