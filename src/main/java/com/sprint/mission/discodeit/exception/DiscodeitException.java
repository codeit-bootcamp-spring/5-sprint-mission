package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;

@Getter
public abstract class DiscodeitException extends RuntimeException {
  private final Instant timestamp = Instant.now();
  private final ErrorCode errorCode;
  private final Map<String, Object> details;

  protected DiscodeitException(ErrorCode code) {
    super(code.getMessage());
    this.errorCode = code;
    this.details = Collections.emptyMap();
  }
  protected DiscodeitException(ErrorCode code, Map<String,Object> details) {
    super(code.getMessage());
    this.errorCode = code;
    this.details = details == null ? Collections.emptyMap() : Map.copyOf(details);
  }
  protected DiscodeitException(ErrorCode code, String message, Map<String,Object> details) {
    super(message == null ? code.getMessage() : message);
    this.errorCode = code;
    this.details = details == null ? Collections.emptyMap() : Map.copyOf(details);
  }
}