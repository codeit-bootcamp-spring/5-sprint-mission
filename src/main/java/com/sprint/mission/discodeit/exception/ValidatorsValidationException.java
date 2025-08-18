package com.sprint.mission.discodeit.exception;

import java.io.Serial;

public class ValidatorsValidationException extends IllegalArgumentException {

  @Serial
  private static final long serialVersionUID = 1L;

  private static final String DEFAULT_MESSAGE = "Validation failed.";

  public ValidatorsValidationException() {
    super(DEFAULT_MESSAGE);
  }

  public ValidatorsValidationException(String message) {
    super(message);
  }

  public ValidatorsValidationException(Throwable cause) {
    super(DEFAULT_MESSAGE, cause);
  }

  public ValidatorsValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
