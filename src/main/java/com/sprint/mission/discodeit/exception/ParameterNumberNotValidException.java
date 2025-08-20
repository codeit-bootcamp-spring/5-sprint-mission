package com.sprint.mission.discodeit.exception;

import java.util.List;
import lombok.Getter;

@Getter
public class ParameterNumberNotValidException extends RuntimeException {

  private final List<String> receivedParameters;

  public ParameterNumberNotValidException(List<String> receivedParameters, String message) {
    super(message);
    this.receivedParameters = (receivedParameters == null)
        ? List.of()
        : List.copyOf(receivedParameters);
  }

  public ParameterNumberNotValidException(List<String> receivedParameters) {
    this(receivedParameters, null);
  }
}
