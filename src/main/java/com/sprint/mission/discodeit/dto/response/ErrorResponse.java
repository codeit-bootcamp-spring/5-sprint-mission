package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
    String timestamp,
    int status,
    String error,
    String message,
    String path,
    List<FieldError> fieldErrors
) {

  //Error 없을때
  public static ErrorResponse of(int status, String error, String message, String path) {
    return new ErrorResponse(Instant.now().toString(), status, error, message, path, null);
  }

  //Error 있을때
  public static ErrorResponse of(int status, String error, String message, String path,
      List<FieldError> fieldErrors) {
    return new ErrorResponse(Instant.now().toString(), status, error, message, path, fieldErrors);
  }

  //ErrorResponse fieldErrors 필드의 List 형태로 들어감
  public record FieldError(String field, String reason) {

  }
}
