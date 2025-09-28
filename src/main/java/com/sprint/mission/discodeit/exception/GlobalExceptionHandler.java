package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.base.DiscodeitException;
import com.sprint.mission.discodeit.exception.base.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleException(DiscodeitException e) {
    e.printStackTrace();

    ErrorResponse errorResponse = new ErrorResponse(
            e.getTimestamp(),
            e.getErrorCode().name(),
            e.getErrorCode().getMessage(),
            e.getDetails(),
            e.getClass().getSimpleName(),
            e.getErrorCode().getStatus()
    );

    return ResponseEntity
            .status(HttpStatus.valueOf(errorResponse.status()))
            .body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
    Map<String, Object> details = new HashMap<>();

    e.getBindingResult().getFieldErrors().forEach(fieldError -> {
      details.put(fieldError.getField(), fieldError.getDefaultMessage());
    });

    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            "VALIDATION_ERROR",
            "유효성 검증 실패",
            details,
            e.getClass().getSimpleName(),
            HttpStatus.BAD_REQUEST.value()
    );

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception e) {
    e.printStackTrace();
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(e.getMessage());
  }
}
