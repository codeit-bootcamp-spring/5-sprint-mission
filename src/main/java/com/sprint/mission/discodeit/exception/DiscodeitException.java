package com.sprint.mission.discodeit.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Getter
public class DiscodeitException extends RuntimeException {

    private final Instant timestamp;
    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public DiscodeitException(
        ErrorCode errorCode,
        Map<String, Object> details
    ) {
        super(Objects.requireNonNull(errorCode).getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details != null
            ? Collections.unmodifiableMap(details)
            : Collections.emptyMap();
    }

    public DiscodeitException(ErrorCode errorCode) {
        this(errorCode, Collections.emptyMap());
    }
}
