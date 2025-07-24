package com.sprint.mission.discodeit.exception;

public class ValidationException extends IllegalArgumentException {
  public ValidationException() {
    super("Validation failed.");
  }

  public ValidationException(String message) {
    super(message);
  }

  public ValidationException(Throwable cause) {
    super(cause);
  }

  public ValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
