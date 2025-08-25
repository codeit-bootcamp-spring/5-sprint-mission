package com.sprint.mission.discodeit.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExceptionResponse {

  private String message;
  private Integer code;
}
