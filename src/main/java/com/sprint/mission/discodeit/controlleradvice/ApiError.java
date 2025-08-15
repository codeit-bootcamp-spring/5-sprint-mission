package com.sprint.mission.discodeit.controlleradvice;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        String code,
        String message,
        List<String> errors) {

    public ApiError(String code, String message, List<String> errors) {
        this(Instant.now(), code, message, errors);
    }
}
