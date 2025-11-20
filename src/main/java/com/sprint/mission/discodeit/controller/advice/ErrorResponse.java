package com.sprint.mission.discodeit.controller.advice;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
    Instant timestamp,
    String code,
    String message,
    Map<String, Object> details,
    String exceptionType,
    int status,
    String requestId
) {

    public static ErrorResponse of(
        String code,
        String message,
        Map<String, Object> details,
        Throwable exception,
        HttpStatus httpStatus,
        String requestId
    ) {
        return new ErrorResponse(
            Instant.now(),
            code,
            message,
            details,
            exception != null ? exception.getClass().getSimpleName() : null,
            httpStatus.value(),
            requestId
        );
    }
}
