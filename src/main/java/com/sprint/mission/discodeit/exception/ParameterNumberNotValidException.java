package com.sprint.mission.discodeit.exception;

import java.util.List;
import lombok.Getter;

@Getter
public class ParameterNumberNotValidException extends RuntimeException {

  private final List<String> receivedParameters;

  public ParameterNumberNotValidException(List<String> receivedParameters, String message) {
    super(message != null && !message.isBlank() ? message : "Multiple parameters not allowed");
    this.receivedParameters = List.copyOf(receivedParameters);
  }

  public ParameterNumberNotValidException(List<String> receivedParameters) {
    this(receivedParameters, null);
  }
}
