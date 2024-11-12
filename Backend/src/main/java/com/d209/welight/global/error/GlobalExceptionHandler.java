package com.d209.welight.global.error;

import com.d209.welight.global.exception.cheer.CheerAccessDeniedException;
import com.d209.welight.global.exception.cheer.CheerNotFoundException;
import com.d209.welight.global.exception.cheer.InvalidCheerDataException;
import com.d209.welight.global.exception.display.DisplayNotFoundException;
import com.d209.welight.global.exception.display.InvalidDisplayDataException;
import com.d209.welight.global.exception.elasticsearch.NoSearchResultException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("EntityNotFoundException", e);
        return createErrorResponse(CommonErrorCode.ENTITY_NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException", e);
        return createErrorResponse(CommonErrorCode.INVALID_PARAMETER, e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        log.error("IllegalStateException", e);
        return createErrorResponse(CommonErrorCode.INVALID_STATE, e.getMessage());
    }

    @ExceptionHandler(NoSearchResultException.class)
    public ResponseEntity<ErrorResponse> handleNoSearchResultException(NoSearchResultException e) {
        log.error("NoSearchResultException", e);
        return createErrorResponse(CommonErrorCode.NO_SEARCH_RESULT, e.getMessage());
    }

    @ExceptionHandler(DisplayNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDisplayNotFoundException(DisplayNotFoundException e) {
        log.error("DisplayNotFoundException", e);
        return createErrorResponse(CommonErrorCode.NO_FOUND_RESULT, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unhandled exception occurred: ", e);
        return createErrorResponse(CommonErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidDisplayDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDisplayDataException(InvalidDisplayDataException e) {
        log.error("InvalidDisplayDataException", e);
        return createErrorResponse(CommonErrorCode.INVALID_DISPLAY_DATA);
    }

    // cheerroom
    @ExceptionHandler(CheerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCheerNotFoundException(CheerNotFoundException e) {
        log.error("CheerNotFoundException", e);
        return createErrorResponse(CommonErrorCode.NO_FOUND_RESULT, e.getMessage());
    }

    @ExceptionHandler(InvalidCheerDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCheerDataException(InvalidCheerDataException e) {
        log.error("InvalidCheerDataException", e);
        return createErrorResponse(CommonErrorCode.INVALID_PARAMETER, e.getMessage());
    }

    @ExceptionHandler(CheerAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleCheerAccessDeniedException(CheerAccessDeniedException e) {
        log.error("CheerAccessDeniedException", e);
        return createErrorResponse(CommonErrorCode.FORBIDDEN, e.getMessage());
    }
    private ResponseEntity<ErrorResponse> createErrorResponse(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ErrorResponse(errorCode));
    }

    // 커스텀 에러 메시지를 받을 때
    private ResponseEntity<ErrorResponse> createErrorResponse(ErrorCode errorCode, String customMessage) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ErrorResponse(errorCode, customMessage));
    }

}