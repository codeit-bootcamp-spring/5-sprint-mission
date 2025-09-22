package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleCustomException(DiscodeitException e) {
    log.error("커스텀 예외 발생: code={}, message={}", e.getErrorCode(), e.getMessage());
    return respond(e);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
    ErrorResponse errorResponse = new ErrorResponse(e, 500);
    return ResponseEntity.status(errorResponse.status()).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    log.error("요청 유효성 검증 실패: {}", e.getMessage(), e);

    Map<String, Object> errors = new HashMap<>();
    e.getBindingResult().getFieldErrors().forEach((fieldError) ->
        errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
    ErrorCode code = ErrorCode.VALIDATION_ERROR;

    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        code.name(),
        code.getMessage(),
        errors,
        e.getClass().getSimpleName(),
        code.getStatus().value()
    );

    return ResponseEntity.status(response.status()).body(response);
  }

  private ResponseEntity<ErrorResponse> respond(DiscodeitException e) {
    return ResponseEntity.status(e.getErrorCode().getStatus().value()).body(new ErrorResponse(e));
  }
}
