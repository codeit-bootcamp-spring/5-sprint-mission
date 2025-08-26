package com.sprint.mission.discodeit.exception;

public class UnauthorizedException extends RuntimeException {

  public UnauthorizedException(String message) {
    super(message);
  }

  public UnauthorizedException() {
    super("Invalid credentials");
  }
}
