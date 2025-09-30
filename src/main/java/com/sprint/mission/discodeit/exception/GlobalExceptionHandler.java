package com.sprint.mission.discodeit.exception;

import java.net.URI;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ProblemDetail handleException(DiscodeitException ex, WebRequest request) {
    ErrorCode errorCode = ex.getErrorCode();
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(errorCode.getStatus(), errorCode.getMessage());

    pd.setTitle(errorCode.getCode());
    pd.setDetail(errorCode.getMessage());
    pd.setStatus(errorCode.getStatus());

    return pd;
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleException(IllegalArgumentException e) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage() != null ? e.getMessage() : "잘못된 요청입니다.");

    pd.setTitle("INVALID_ARGUMENT");
    pd.setType(URI.create("https://api.discodeit.com/errors/invalid-argument"));
    pd.setProperty("correlationId", UUID.randomUUID().toString());
    pd.setProperty("timestamp", Instant.now().toString());

    return pd;
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ProblemDetail handleException(NoSuchElementException e) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            e.getMessage() != null ? e.getMessage() : "요청한 리소스를 찾을 수 없습니다."
    );

    pd.setTitle("NOT_FOUND");
    pd.setType(URI.create("https://api.discodeit.com/errors/not-found"));
    pd.setProperty("correlationId", UUID.randomUUID().toString());
    pd.setProperty("timestamp", Instant.now().toString());

    return pd;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleException(Exception e) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            e.getMessage() != null ? e.getMessage() : "예상치 못한 서버 오류가 발생했습니다."
    );

    pd.setTitle("INTERNAL_ERROR");
    pd.setType(URI.create("https://api.discodeit.com/errors/internal"));
    pd.setProperty("correlationId", UUID.randomUUID().toString());
    pd.setProperty("timestamp", Instant.now().toString());

    return pd;
  }
}
