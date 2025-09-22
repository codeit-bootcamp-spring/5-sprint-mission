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
        ex.getDetails(),
        ex.getClass().getSimpleName(),
        ex.getErrorCode().getStatus().value());
  }

  public ErrorResponse(Exception ex, int status) {
    this(Instant.now(),
        ex.getClass().getSimpleName(),
        ex.getMessage(),
        new HashMap<>(),
        ex.getClass().getSimpleName(),
        status);
  }
}
