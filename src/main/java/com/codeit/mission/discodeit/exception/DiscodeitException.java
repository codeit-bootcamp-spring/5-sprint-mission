package com.codeit.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class DiscodeitException extends RuntimeException {

    private final Instant timestamp;
    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public DiscodeitException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = new HashMap<>();
    }

    public DiscodeitException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = new HashMap<>();
    }

    public DiscodeitException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = new HashMap<>();
    }

    public DiscodeitException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = new HashMap<>();
    }

    public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = new HashMap<>(details);
    }

    public DiscodeitException(ErrorCode errorCode, String customMessage,
            Map<String, Object> details) {
        super(customMessage);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = new HashMap<>(details);
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Map<String, Object> getDetails() {
        return new HashMap<>(details);
    }

    public void addDetail(String key, Object value) {
        this.details.put(key, value);
    }

    public void addDetails(Map<String, Object> details) {
        this.details.putAll(details);
    }
}