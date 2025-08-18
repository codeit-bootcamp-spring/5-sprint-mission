package com.sprint.mission.discodeit.controller.advice;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

@JsonInclude(NON_EMPTY)
public record ApiError(
    Instant timestamp,
    String path,
    String method,
    int status,
    String code,
    String message,
    List<String> details
) {

  public ApiError {
    if (timestamp == null) {
      timestamp = Instant.now();
    }
    if (method != null) {
      method = method.toUpperCase();
    }
    details = (details == null) ? List.of() : List.copyOf(details);
    path = (path == null) ? "" : path;
    code = (code == null) ? "INTERNAL_ERROR" : code;
    message = (message == null) ? "" : message;
  }

  public ApiError(String path, String method, int status, String code, String message,
      List<String> details) {
    this(null, path, method, status, code, message, details);
  }
}
