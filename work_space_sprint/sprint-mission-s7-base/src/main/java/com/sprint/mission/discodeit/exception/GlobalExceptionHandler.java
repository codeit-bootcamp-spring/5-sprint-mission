package com.sprint.mission.discodeit.exception;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    log.error("DiscodeitException 발생: {}", e.getMessage(), e);

    HttpStatus status = getHttpStatusFromErrorCode(e.getErrorCode());
    ErrorResponse errorResponse = ErrorResponse.of(e, status.value());

    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    log.error("IllegalArgumentException 발생: {}", e.getMessage(), e);

    ErrorResponse errorResponse = ErrorResponse.of(
        "INVALID_INPUT",
        e.getMessage(),
        HttpStatus.BAD_REQUEST.value()
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
    log.warn("유효성 검증 실패: {}", e.getMessage());

    Map<String, Object> details = new HashMap<>();
    e.getBindingResult().getFieldErrors().forEach(error ->
        details.put(error.getField(), error.getDefaultMessage())
    );

    ErrorResponse errorResponse = ErrorResponse.of(
        "VALIDATION_FAILED",
        "입력값 검증에 실패했습니다.",
        details,
        HttpStatus.BAD_REQUEST.value()
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("예상치 못한 예외 발생: {}", e.getMessage(), e);

    ErrorResponse errorResponse = ErrorResponse.of(
        "INTERNAL_SERVER_ERROR",
        "서버 내부 오류가 발생했습니다.",
        HttpStatus.INTERNAL_SERVER_ERROR.value()
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  private HttpStatus getHttpStatusFromErrorCode(ErrorCode errorCode) {
    return switch (errorCode) {
      case USER_NOT_FOUND, CHANNEL_NOT_FOUND, MESSAGE_NOT_FOUND, BINARY_CONTENT_NOT_FOUND ->
          HttpStatus.NOT_FOUND;
      case DUPLICATE_USER, PRIVATE_CHANNEL_UPDATE, WRONG_PASSWORD, INVALID_INPUT ->
          HttpStatus.BAD_REQUEST;
      case INTERNAL_SERVER_ERROR ->
          HttpStatus.INTERNAL_SERVER_ERROR;
      default ->
          HttpStatus.BAD_REQUEST;
    };
  }
}