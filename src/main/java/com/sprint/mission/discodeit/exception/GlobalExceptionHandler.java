package com.sprint.mission.discodeit.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /** 401/404/409 등 원래 의도한 상태코드를 그대로 유지 */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<String> handle(ResponseStatusException e) {
    log.warn("ResponseStatusException: {} {}", e.getStatusCode(), e.getReason(), e);
    String body = e.getReason() != null ? e.getReason() : e.getMessage();
    return ResponseEntity.status(e.getStatusCode()).body(body);
  }

  /** 유효성 검증 실패 → 400 */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handle(MethodArgumentNotValidException e) {
    log.warn("Validation failed", e);
    var msg = e.getBindingResult().getFieldErrors().stream()
        .findFirst()
        .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
        .orElse("Bad request");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
  }

  /** 중복/제약 위반 → 409 */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<String> handle(DataIntegrityViolationException e) {
    log.warn("Constraint violation", e);
    return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict");
  }

  /** 잘못된 요청 → 400 */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handle(IllegalArgumentException e) {
    log.warn("Illegal argument", e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  /** 리소스 없음 → 404 */
  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<String> handle(NoSuchElementException e) {
    log.warn("Not found", e);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
  }

  /** 마지막 보루만 500 */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handle(Exception e) {
    log.error("Unexpected error", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
  }
}
