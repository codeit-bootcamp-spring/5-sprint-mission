package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
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

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex,
      HttpServletRequest request) {

    log.error("Exception: {}", ex.getMessage());

    return ResponseEntity.internalServerError()
                         .body(ErrorResponse.builder()
                                            .timestamp(Instant.now())
                                            .status(500)
                                            .message(ex.getMessage())
                                            .path(request.getRequestURI())
                                            .build());
  }

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleBusinessExceptions(DiscodeitException ex,
      HttpServletRequest request) {

    log.error("DiscodeitException: {}", ex.getMessage());

    return new ResponseEntity<>(ErrorResponse.builder()
                                             .timestamp(ex.getTimestamp())
                                             .status(ex.getErrorCode()
                                                       .getStatus())
                                             .message(ex.getMessage())
                                             .details(ex.getDetails())
                                             .exceptionType(ex.getClass()
                                                              .getSimpleName())
                                             .path(request.getRequestURI())
                                             .build(), HttpStatus.valueOf(ex.getErrorCode()
                                                                            .getStatus()));
  }


  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    log.error("MethodArgumentNotValidException: {}", ex.getMessage());

    Map<String, Object> details = new HashMap<>();

    ex.getBindingResult()
      .getFieldErrors()
      .forEach(error -> details.put(error.getField(), error.getDefaultMessage()));

    ErrorResponse response = ErrorResponse.builder()
                                          .timestamp(Instant.now())
                                          .status(ErrorCode.VALIDATION_ERROR.getStatus())
                                          .message(ErrorCode.VALIDATION_ERROR.getMessage())
                                          .details(details)
                                          .exceptionType(ex.getClass()
                                                           .getSimpleName())
                                          .path(request.getRequestURI())
                                          .build();

    return ResponseEntity.badRequest()
                         .body(response);
  }
}
