package com.sprint.mission.discodeit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    log.error("예상치 못한 오류 발생 : {}", e.getMessage(), e);
    ErrorResponse errorResponse = new ErrorResponse(e, 500);
    return ResponseEntity.status(errorResponse.status()).body(errorResponse);
  }

  private ResponseEntity<ErrorResponse> respond(DiscodeitException e) {
    return ResponseEntity.status(e.getErrorCode().getStatus().value()).body(new ErrorResponse(e));
  }
}
