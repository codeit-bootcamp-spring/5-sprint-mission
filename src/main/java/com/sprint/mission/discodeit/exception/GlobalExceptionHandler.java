package com.sprint.mission.discodeit.exception;

<<<<<<< HEAD
import java.util.HashMap;
=======
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
<<<<<<< HEAD
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
    Map<String, Object> body = new HashMap<>();
    body.put("status", status.value());
    body.put("message", message);
    return new ResponseEntity<>(body, status);
  }

  // 데이터 없을 때
  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<Map<String, Object>> handleNoSuchElement(NoSuchElementException ex) {
    return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  // 런타임 예외
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류: " + ex.getMessage());
  }

  // 잘못된 인자 전달
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  // @Valid 검증 실패
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .findFirst()
        .orElse(ex.getMessage());
    return buildResponse(HttpStatus.BAD_REQUEST, message);
  }

  // 요청 유효하지 않음
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
    return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
  }

  // 알 수 없는 예외
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류: " + ex.getMessage());
  }


=======
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    return buildErrorResponse(e, mapStatus(e.getErrorCode()), null);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
    return buildErrorResponse(e, HttpStatus.NOT_FOUND, null);
  }

  @ExceptionHandler(MessageNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleMessageNotFound(MessageNotFoundException e) {
    return buildErrorResponse(e, HttpStatus.NOT_FOUND, null);
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException e) {
    return buildErrorResponse(e, HttpStatus.UNAUTHORIZED, null);
  }

  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<ErrorResponse> handleMissingPart(MissingServletRequestPartException e) {
    return buildErrorResponse(e, HttpStatus.BAD_REQUEST, Map.of("part", e.getRequestPartName()));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException e) {
    return buildErrorResponse(e, HttpStatus.BAD_REQUEST, null);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
    return buildErrorResponse(e, HttpStatus.BAD_REQUEST,
        Map.of("name", e.getName(), "value", String.valueOf(e.getValue())));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAnyException(Exception e) {
    return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, null);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException e) {
    return buildErrorResponse(e, HttpStatus.NOT_FOUND, null);
  }

  private ResponseEntity<ErrorResponse> buildErrorResponse(Exception e, HttpStatus status, Map<String, Object> details) {
    ErrorCode errorCode = (e instanceof DiscodeitException de) ? de.getErrorCode() : ErrorCode.INTERNAL_ERROR;
    ErrorResponse body = ErrorResponse.builder()
        .status(status.value())
        .code(errorCode.name())
        .message(e.getMessage())
        .exceptionType(e.getClass().getSimpleName())
        .details(details)
        .timestamp(Instant.now())
        .build();
    return ResponseEntity.status(status).body(body);
  }

  private HttpStatus mapStatus(ErrorCode code) {
    return switch (code) {
      case INVALID_ARGUMENT -> HttpStatus.BAD_REQUEST;
      case NOT_FOUND,
           USER_NOT_FOUND,
           CHANNEL_NOT_FOUND,
           MESSAGE_NOT_FOUND,
           READ_STATUS_NOT_FOUND,
           USER_STATUS_NOT_FOUND,
           BINARY_CONTENT_NOT_FOUND
          -> HttpStatus.NOT_FOUND;
      case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
      case USER_ALREADY_EXISTS,
           READ_STATUS_ALREADY_EXISTS
          -> HttpStatus.CONFLICT;
      default -> HttpStatus.INTERNAL_SERVER_ERROR;
    };
  }
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
}
