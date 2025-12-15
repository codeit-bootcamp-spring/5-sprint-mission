package com.sprint.mission.discodeit.common.domain.exception;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
public class DiscodeitException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public DiscodeitException(ErrorCode errorCode) {
        this(errorCode, Collections.emptyMap(), null);
    }

    public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
        this(errorCode, details, null);
    }

    public DiscodeitException(ErrorCode errorCode, Throwable cause) {
        this(errorCode, null, cause);
    }

    public DiscodeitException(
        ErrorCode errorCode,
        Map<String, Object> details,
        Throwable cause
    ) {
        super(errorCode.getMessage(), cause);

        this.errorCode = errorCode;
        this.details = details != null
            ? Collections.unmodifiableMap(details)
            : Collections.emptyMap();
    }
}
