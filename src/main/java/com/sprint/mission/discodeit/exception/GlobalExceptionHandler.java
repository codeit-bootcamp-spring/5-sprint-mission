package com.sprint.mission.discodeit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handlerException(Exception e) {
        log.error("예상치 못한 오류 발생 : {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(e, 500);
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    // 커스텀 에러 처리
    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handlerCustomException(DiscodeitException e) {
        log.error("커스텀 예외 발생 : code={}, message={}", e.getErrorCode(), e.getMessage());
        HttpStatus httpStatus = determineHttpStatus(e);
        ErrorResponse errorResponse = new ErrorResponse(e, httpStatus.value());
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    private HttpStatus determineHttpStatus(DiscodeitException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return switch (errorCode) {
            case USER_NOT_FOUND,
                 CHANNEL_NOT_FOUND,
                 MESSAGE_NOT_FOUND,
                 BINARY_CONTENT_NOT_FOUND,
                 READ_STATUS_NOT_FOUND,
                 USER_STATUS_NOT_FOUND,
                 FILE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case DUPLICATE_USER,
                 DUPLICATE_READ_STATUS,
                 DUPLICATE_USER_STATUS,
                 DUPLICATE_FILE -> HttpStatus.CONFLICT;
            case INVALID_USER_CREDENTIALS -> HttpStatus.UNAUTHORIZED;
            case PRIVATE_CHANNEL_UPDATE -> HttpStatus.BAD_REQUEST;
        };
    }
}
