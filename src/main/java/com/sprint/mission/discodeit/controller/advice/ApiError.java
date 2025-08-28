package com.sprint.mission.discodeit.controller.advice;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
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
        List<String> details
    ) {
        return new ApiError(
            Instant.now(),
            req.getRequestURI(),
            req.getMethod(),
            httpStatus.value(),
            code,
            message,
            details
        );
    }
}
