package com.sprint.mission.discodeit.controller.advice;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiError(
    Instant timestamp,
    String path,
    String method,
    int status,
    String code,
    String message,
    List<String> details
) {

  public static ApiError from(
      HttpServletRequest req,
      HttpStatus httpStatus,
      String code,
      String message,
      Collection<String> details
  ) {
    Objects.requireNonNull(req, "req must not be null");
    String path = req.getRequestURI() != null ? req.getRequestURI() : "";
    String method = req.getMethod() != null ? req.getMethod().toUpperCase(Locale.ROOT) : "";
    int status = httpStatus != null ? httpStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR.value();
    String resolvedCode = (code != null && !code.isBlank()) ? code : "INTERNAL_ERROR";
    String resolvedMessage = message != null ? message : "";

    List<String> normalizedDetails =
        details == null
            ? List.of()
            : details.stream()
                .filter(Objects::nonNull)
                .map(String::strip)
                .filter(s -> !s.isEmpty())
                .toList();

    return new ApiError(
        Instant.now(),
        path,
        method,
        status,
        resolvedCode,
        resolvedMessage,
        normalizedDetails
    );
  }
}
