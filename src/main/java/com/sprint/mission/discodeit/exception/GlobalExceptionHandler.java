package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.storage.StorageException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleCustomException(DiscodeitException e) {
    log.warn("커스텀 예외 발생: code={}, message={}, details={}",
        e.getErrorCode(), e.getMessage(), e.getDetails());

    ErrorResponse body = new ErrorResponse(e);
    return ResponseEntity.status(body.status()).body(body);
  }

  @ExceptionHandler(StorageException.class)
  public ResponseEntity<ErrorResponse> handleStorageException(StorageException e) {
    log.error("파일 처리 오류 발생: code={}, message={}", e.getErrorCode(), e.getMessage(), e);
    ErrorResponse body = ErrorResponse.from(e, "path", "root");
    return ResponseEntity.status(body.status()).body(body);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    log.warn("요청 유효성 검증 실패: {}", e.getMessage());

    Map<String, Object> errors = new HashMap<>();
    e.getBindingResult().getFieldErrors().forEach((fieldError) ->
        errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
    ErrorCode code = ErrorCode.VALIDATION_ERROR;

    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        code.name(),
        code.getMessage(),
        errors,
        e.getClass().getSimpleName(),
        code.getStatus().value()
    );

    return ResponseEntity.status(response.status()).body(response);
  }

  @ExceptionHandler({
      BindException.class,
      MethodArgumentTypeMismatchException.class,
      HttpMessageNotReadableException.class,
      MissingServletRequestPartException.class
  })
  public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
    log.warn("요청 파싱/바인싱 실패: {}", e.getMessage());
    ErrorCode code = ErrorCode.VALIDATION_ERROR;

    return ResponseEntity
        .status(code.getStatus())
        .body(
            new ErrorResponse(
                Instant.now(),
                code.name(),
                code.getMessage(),
                Map.of(),
                e.getClass().getSimpleName(),
                code.getStatus().value()
            )
        );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
    ErrorResponse errorResponse = new ErrorResponse(e, 500);
    return ResponseEntity.status(errorResponse.status()).body(errorResponse);
  }
}
