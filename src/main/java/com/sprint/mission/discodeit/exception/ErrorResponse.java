package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
  private final Instant timestamp;           // 발생 시각
  private final String code;                 // 에러 이름
  private final String message;              // 메시지
  private final Map<String, Object> details; // 부가 정보
  private final String exceptionType;        // 예외 클래스 이름
  private final int status;                  // HTTP 상태 코드

  public static ErrorResponse of(DiscodeitException e) {
    return ErrorResponse.builder()
        .timestamp(e.getTimestamp())
        .code(e.getErrorCode().name())
        .message(e.getMessage())
        .details(e.getDetails())
        .exceptionType(e.getClass().getSimpleName())
        .status(e.getErrorCode().getStatus().value())
        .build();
  }
}