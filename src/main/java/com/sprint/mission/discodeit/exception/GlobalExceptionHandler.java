package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
