package com.sprint.mission.discodeit.controller.advice;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        String code,
        String message,
        List<String> details) {

    public ApiError(String code, String message, List<String> details) {
        this(Instant.now(), code, message, details);
    }
}
