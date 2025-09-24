package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

public class DiscodeitException extends RuntimeException {

  /* 모든 도메인별 예외의 부모 클래스
   * ErrorCode(Enum)만 받게 설계해서, 코드/메세지 통일
   */

  private final Instant timestamp; //예외 발생 시간
  private final ErrorCode errorCode; // 어떤 에러코드(메시지 포함)
  private final Map<String, Object> details; // 예외 관련 추가 정보(예: userId)


  // 생성자
  public DiscodeitException(ErrorCode errorCode) {
    this(errorCode, Collections.emptyMap());    // details는 옵션임
  }

  public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode.getMessage()); // 예외 메시지 설정 (부모에게 전달)
    this.timestamp = Instant.now();
    this.errorCode = errorCode;
    this.details = details != null ? details : Collections.emptyMap();
  }

  // getter
  public Instant getTimestamp() {
    return timestamp;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  public Map<String, Object> getDetails() {
    return details;
  }
}