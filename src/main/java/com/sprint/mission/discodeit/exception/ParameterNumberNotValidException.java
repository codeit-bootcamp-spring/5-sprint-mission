package com.sprint.mission.discodeit.exception;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class ParameterNumberNotValidException extends RuntimeException {

    private final List<String> receivedParameters;

    public ParameterNumberNotValidException(List<String> receivedParameters) {
        this(receivedParameters, null);
    }

    public ParameterNumberNotValidException(List<String> receivedParameters, String message) {
        super(message != null && !message.isBlank() ? message : "Multiple parameters not allowed");
        this.receivedParameters = Collections.unmodifiableList(receivedParameters);
    }
}
