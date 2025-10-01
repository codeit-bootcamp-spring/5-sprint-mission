package com.sprint.mission.discodeit.exception;

import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder
public record ErrorResponse(
    int status,
    String code,
    String message,
    String exceptionType,
    Map<String, Object> details,
    Instant timestamp
) {}
