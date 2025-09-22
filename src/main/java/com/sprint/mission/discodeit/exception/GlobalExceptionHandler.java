package com.sprint.mission.discodeit.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeit(DiscodeitException e) {
    ErrorResponse body = ErrorResponse.of(e);
    log.warn("[EX] code={} status={} message={} details={}",
        body.getCode(), body.getStatus(), body.getMessage(), body.getDetails());
    return ResponseEntity.status(body.getStatus()).body(body);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
    BindingResult br = e.getBindingResult();
    Map<String, Object> details = br.getFieldErrors().stream()
        .collect(Collectors.toMap(
            fe -> fe.getField(),
            fe -> fe.getDefaultMessage(),
            (a, b) -> a
        ));
    ErrorResponse body = ErrorResponse.of(ErrorCode.INVALID_REQUEST,
        "요청 데이터가 올바르지 않습니다.", details, e.getClass());
    log.warn("[EX][VALID] {}", details);
    return ResponseEntity.status(body.getStatus()).body(body);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException e) {
    Map<String, Object> details = e.getConstraintViolations().stream()
        .collect(Collectors.toMap(
            v -> v.getPropertyPath().toString(),
            v -> v.getMessage(),
            (a, b) -> a
        ));
    ErrorResponse body = ErrorResponse.of(ErrorCode.INVALID_REQUEST,
        "요청 데이터가 올바르지 않습니다.", details, e.getClass());
    log.warn("[EX][CONSTRAINT] {}", details);
    return ResponseEntity.status(body.getStatus()).body(body);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException e) {
    ErrorResponse body = ErrorResponse.of(ErrorCode.INVALID_REQUEST,
        "요청 본문을 읽을 수 없습니다.", Map.of("reason", e.getMostSpecificCause().getMessage()), e.getClass());
    log.warn("[EX][NOT_READABLE] {}", body.getMessage());
    return ResponseEntity.status(body.getStatus()).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAny(Exception e) {
    log.error("Unhandled exception", e);
    ErrorResponse body = ErrorResponse.of(ErrorCode.INTERNAL_ERROR,
        e.getMessage(), Map.of(), e.getClass());
    return ResponseEntity.status(body.getStatus()).body(body);
  }
}
