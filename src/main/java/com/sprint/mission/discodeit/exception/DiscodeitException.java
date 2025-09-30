package com.sprint.mission.discodeit.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

@Getter
public class DiscodeitException extends RuntimeException {
    private final Instant timestamp = Instant.now();
    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public DiscodeitException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = Collections.emptyMap();
    }

    public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details != null ? details : Collections.emptyMap();
    }
}
