package com.sprint.mission.discodeit.controller.advice;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiError(
    Instant timestamp,
    String code,
    String message,
    Map<String, Object> details,
    String exceptionType,
    int status,
    String requestId
) {

    public static ApiError of(
        String code,
        String message,
        Map<String, Object> details,
        Throwable exception,
        HttpStatus httpStatus,
        String requestId
    ) {
        return new ApiError(
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
