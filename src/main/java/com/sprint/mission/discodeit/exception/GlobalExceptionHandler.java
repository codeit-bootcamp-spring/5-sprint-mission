package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // --- 400 계열 ---

  // 클라이언트 입력값 오류(너가 던진 IllegalArgumentException 포함)
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> badRequest(IllegalArgumentException e) {
    return body(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  // DTO 검증 실패 (@Valid 없으면 보통 발생 안 함, 있어도 안전하게 매핑)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException e) {
    String msg = e.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
            .findFirst().orElse("Validation error");
    return body(HttpStatus.BAD_REQUEST, msg);
  }

  // JSON 파싱/바인딩 실패
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, Object>> unreadable(HttpMessageNotReadableException e) {
    return body(HttpStatus.BAD_REQUEST, "Malformed JSON request");
  }

  // 멀티파트에서 필요한 파트 누락 (예: userCreateRequest 빠짐)
  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<Map<String, Object>> missingPart(MissingServletRequestPartException e) {
    return body(HttpStatus.BAD_REQUEST, "Missing part: " + e.getRequestPartName());
  }

  // 경로변수/쿼리파라미터 타입 불일치 (예: UUID 형식 아님)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Map<String, Object>> typeMismatch(MethodArgumentTypeMismatchException e) {
    return body(HttpStatus.BAD_REQUEST, "Invalid parameter: " + e.getName());
  }

  // 업로드 용량 초과
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<Map<String, Object>> tooLarge(MaxUploadSizeExceededException e) {
    return body(HttpStatus.PAYLOAD_TOO_LARGE, "Upload size limit exceeded");
  }

  // --- 404 ---

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<Map<String, Object>> notFound(NoSuchElementException e) {
    return body(HttpStatus.NOT_FOUND, e.getMessage());
  }

  // --- 405 ---

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<Map<String, Object>> methodNotAllowed(HttpRequestMethodNotSupportedException e) {
    return body(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed");
  }

  // --- 409 ---

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<Map<String, Object>> conflict(IllegalStateException e) {
    return body(HttpStatus.CONFLICT, e.getMessage());
  }

  // --- 500 및 특례 처리 ---
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> serverError(Exception e) {
    // 특례: 컨트롤러/서비스에서 RuntimeException으로 래핑된 IOException을 400으로 다운그레이드
    Throwable root = rootCause(e);
    if (root instanceof IOException) {
      return body(HttpStatus.BAD_REQUEST, "File read error");
    }
    return body(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
  }

  // 공통 바디 빌더
  private ResponseEntity<Map<String, Object>> body(HttpStatus status, String detail) {
    Map<String, Object> map = new HashMap<>();
    map.put("status", status.value());
    map.put("error", status.getReasonPhrase());
    map.put("detail", detail == null ? "" : detail);
    map.put("timestamp", Instant.now().toString());
    return ResponseEntity.status(status).body(map);
  }

  private static Throwable rootCause(Throwable t) {
    Throwable cur = t;
    while (cur.getCause() != null && cur.getCause() != cur) {
      cur = cur.getCause();
    }
    return cur;
  }
}