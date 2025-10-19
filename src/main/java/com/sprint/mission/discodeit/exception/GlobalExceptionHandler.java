package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j; // ✅ 추가
import org.slf4j.MDC;             // ✅ (traceId 등)
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e, HttpServletRequest req) {
    return buildErrorResponse(e, HttpStatus.NOT_FOUND, null, req);
  }

  @ExceptionHandler(MessageNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleMessageNotFound(MessageNotFoundException e, HttpServletRequest req) {
    return buildErrorResponse(e, HttpStatus.NOT_FOUND, null, req);
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException e, HttpServletRequest req) {
    return buildErrorResponse(e, HttpStatus.UNAUTHORIZED, null, req);
  }

  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<ErrorResponse> handleMissingPart(MissingServletRequestPartException e, HttpServletRequest req) {
    return buildErrorResponse(e, HttpStatus.BAD_REQUEST, Map.of("part", e.getRequestPartName()), req);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException e, HttpServletRequest req) {
    return buildErrorResponse(e, HttpStatus.BAD_REQUEST, null, req);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest req) {
    return buildErrorResponse(e, HttpStatus.BAD_REQUEST,
        Map.of("name", e.getName(), "value", String.valueOf(e.getValue())), req);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException e, HttpServletRequest req) {
    return buildErrorResponse(e, HttpStatus.NOT_FOUND, null, req);
  }

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e, HttpServletRequest req) {
    return buildErrorResponse(e, mapStatus(e.getErrorCode()), null, req);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAnyException(Exception e, HttpServletRequest req) {
    return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, null, req);
  }

  private ResponseEntity<ErrorResponse> buildErrorResponse(Exception e,
      HttpStatus status,
      Map<String, Object> details,
      HttpServletRequest req) {
    ErrorCode errorCode = (e instanceof DiscodeitException de) ? de.getErrorCode() : ErrorCode.INTERNAL_ERROR;

    String traceId = Optional.ofNullable(MDC.get("traceId"))
        .orElseGet(() -> Optional.ofNullable(req.getHeader("X-Request-Id")).orElse("-"));
    String method = req.getMethod();
    String path   = req.getRequestURI();
    String query  = Optional.ofNullable(req.getQueryString()).orElse("");
    String remote = Optional.ofNullable(req.getRemoteAddr()).orElse("-");
    String userId = Optional.ofNullable(req.getUserPrincipal()).map(p -> p.getName()).orElse("-");

    String baseMsg = String.format(
        "Handled exception: status=%d code=%s ex=%s msg=%s traceId=%s method=%s path=%s query=%s remote=%s user=%s",
        status.value(), errorCode.name(), e.getClass().getSimpleName(), safeMsg(e.getMessage()),
        traceId, method, path, query, remote, userId
    );

    if (status.is5xxServerError()) {
      log.error(baseMsg, e);
    } else {
      log.warn(baseMsg);
    }

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

  private String safeMsg(String msg) {
    if (msg == null) return null;
    String m = msg;
    m = m.replaceAll("(?i)(password|pwd)=[^&\\s]+", "$1=****");
    m = m.replaceAll("(?i)(access[_-]?token|refresh[_-]?token)=[^&\\s]+", "$1=****");
    return m;
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
}