package com.sprint.mission.discodeit.exception;

public class ValidatorsValidationException extends IllegalArgumentException {
    public ValidatorsValidationException() {
        super("Validation failed.");
    }

    public ValidatorsValidationException(String message) {
        super(message);
    }

    public ValidatorsValidationException(Throwable cause) {
        super(cause);
    }

    public ValidatorsValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
