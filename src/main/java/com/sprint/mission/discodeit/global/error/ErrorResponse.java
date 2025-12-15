package com.sprint.mission.discodeit.global.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sprint.mission.discodeit.common.domain.exception.DiscodeitException;
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
    int status
) {
    public static ErrorResponse from(DiscodeitException exception) {
        return new ErrorResponse(
            Instant.now(),
            exception.getErrorCode().name(),
            exception.getMessage(),
            exception.getDetails(),
            exception.getClass().getSimpleName(),
            exception.getErrorCode().getHttpStatus().value()
        );
    }

    public static ErrorResponse of(
        String code,
        String message,
        Map<String, Object> details,
        Throwable exception,
        HttpStatus httpStatus
    ) {
        return new ErrorResponse(
            Instant.now(),
            code,
            message,
            details,
            exception != null ? exception.getClass().getSimpleName() : null,
            httpStatus.value()
        );
    }
}
