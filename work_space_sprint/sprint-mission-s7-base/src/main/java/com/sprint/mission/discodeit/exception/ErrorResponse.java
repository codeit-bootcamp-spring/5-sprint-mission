package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
    Instant timestamp,
    String code,
    String message,
    Map<String, Object> details,
    String exceptionType,
    int status
) {
    
    public static ErrorResponse of(DiscodeitException exception, int status) {
        return new ErrorResponse(
            exception.getTimestamp(),
            exception.getErrorCode().name(),
            exception.getMessage(),
            exception.getDetails(),
            exception.getClass().getSimpleName(),
            status
        );
    }
    
    public static ErrorResponse of(String code, String message, int status) {
        return new ErrorResponse(
            Instant.now(),
            code,
            message,
            Map.of(),
            "Exception",
            status
        );
    }
    
    public static ErrorResponse of(String code, String message, Map<String, Object> details, int status) {
        return new ErrorResponse(
            Instant.now(),
            code,
            message,
            details,
            "Exception",
            status
        );
    }
}