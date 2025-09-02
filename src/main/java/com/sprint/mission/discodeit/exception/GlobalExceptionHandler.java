package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // --- 400 계열 ---
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> badRequest(IllegalArgumentException e) {
    return body(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException e) {
    String msg = e.getBindingResult().getFieldErrors().stream()
        .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
        .findFirst().orElse("Validation error");
    return body(HttpStatus.BAD_REQUEST, msg);
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<Map<String, Object>> tooLarge(MaxUploadSizeExceededException e) {
    return body(HttpStatus.PAYLOAD_TOO_LARGE, "Upload size limit exceeded");
  }

  // --- 404 ---
  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<Map<String, Object>> notFound(NoSuchElementException e) {
    return body(HttpStatus.NOT_FOUND, e.getMessage());
  }

  private ResponseEntity<Map<String, Object>> body(HttpStatus status, String detail) {
    Map<String, Object> map = new HashMap<>();
    map.put("status", status.value());
    map.put("error", status.getReasonPhrase());
    map.put("detail", detail == null ? "" : detail);
    map.put("timestamp", Instant.now().toString());
    return ResponseEntity.status(status).body(map);
  }
}
