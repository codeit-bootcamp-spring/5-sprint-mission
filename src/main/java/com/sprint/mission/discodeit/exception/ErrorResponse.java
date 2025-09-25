package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public record ErrorResponse(
    Instant timestamp,
    String code,
    String message,
    Map<String, Object> details,
    String exceptionType,
    int status
) {

  public ErrorResponse(DiscodeitException ex) {
    this(ex.getTimestamp(),
        ex.getErrorCode().name(),
        ex.getMessage(),
        copyOrEmpty(ex.getDetails()),
        ex.getClass().getSimpleName(),
        ex.getErrorCode().getStatus().value());
  }

  public ErrorResponse(Exception ex, int status) {
    this(Instant.now(),
        ex.getClass().getSimpleName(),
        ex.getMessage() != null ? ex.getMessage() : "Unexpected error",
        Map.of(),
        ex.getClass().getSimpleName(),
        status);
  }

  // null 처리 및 불변 사본 처리
  private static Map<String, Object> copyOrEmpty(Map<String, Object> map) {
    return map == null ? Map.of() : Map.copyOf(map);
  }

  // 민감정보 노출 제한
  public static ErrorResponse from(DiscodeitException e, String... redactKeys) {
    Map<String, Object> details =
        new HashMap<>(e.getDetails() == null ? Map.of() : e.getDetails());

    for (String key : redactKeys) {
      details.remove(key);
    }

    return new ErrorResponse(
        e.getTimestamp(),
        e.getErrorCode().name(),
        e.getMessage(),
        Map.copyOf(details),
        e.getClass().getSimpleName(),
        e.getErrorCode().getStatus().value()
    );
  }
}
