package com.sprint.mission.discodeit.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  private static OffsetDateTime nowUtc() {
    return OffsetDateTime.now(ZoneOffset.UTC);
  }

  // 내부 예외 메세지를 노출하지 않기 위해 가공하는 메소드
  private String safeDetail(Exception e) {
    // 추후 필요 시 화이트리스트 기반 메세지 통제
    return e.getMessage();
  }

  @ExceptionHandler({ConstraintViolationException.class, IllegalArgumentException.class})
  public ProblemDetail handleBadRequest(Exception e, HttpServletRequest request) {
    log.warn("Bad Request: {}", e.getMessage(), e);

    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setTitle("Bad Request");
    pd.setDetail(safeDetail(e));
    pd.setInstance(URI.create(request.getRequestURI()));
    pd.setProperty("timestamp", nowUtc());
    pd.setProperty("method", request.getMethod());
    pd.setProperty("code", "BAD_REQUEST");

    return pd;
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ProblemDetail handleNotFound(Exception e, HttpServletRequest request) {
    log.warn("Not Found: {}", e.getMessage(), e);

    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    pd.setTitle("Not Found");
    pd.setDetail(safeDetail(e));
    pd.setInstance(URI.create(request.getRequestURI()));
    pd.setProperty("timestamp", nowUtc());
    pd.setProperty("method", request.getMethod());
    pd.setProperty("code", "NOT_FOUND");

    return pd;
  }

  @ExceptionHandler(SecurityException.class)
  public ProblemDetail handleForbidden(Exception e, HttpServletRequest request) {
    log.warn("Forbidden: {}", e.getMessage(), e);

    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
    pd.setTitle("Forbidden");
    pd.setDetail(safeDetail(e));
    pd.setInstance(URI.create(request.getRequestURI()));
    pd.setProperty("timestamp", nowUtc());
    pd.setProperty("method", request.getMethod());
    pd.setProperty("code", "FORBIDDEN");

    return pd;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleServerError(Exception e, HttpServletRequest request) {
    log.error("Internal Server Error: {}", e.getMessage(), e);

    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    pd.setTitle("Internal Server Error");
    pd.setDetail("서버에서 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    pd.setInstance(URI.create(request.getRequestURI()));
    pd.setProperty("timestamp", nowUtc());
    pd.setProperty("method", request.getMethod());
    pd.setProperty("code", "INTERNAL_SERVER_ERROR");

    return pd;
  }
}
