package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  // User 관련 에러 코드
  USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  DUPLICATE_USER("이미 존재하는 사용자입니다.", HttpStatus.CONFLICT),
  INVALID_USER_CREDENTIALS("잘못된 사용자 인증 정보입니다."),
  INVALID_USER_PARAMETER("잘못된 사용자 파라미터입니다."),

  // UserStatus 관련 에러 코드
  USER_STATUS_NOT_FOUND("사용자 상태를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  DUPLICATE_USER_STATUS("이미 존재하는 사용자 상태입니다.", HttpStatus.CONFLICT),

  // Channel 관련 에러 코드
  CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  INVALID_CHANNEL_UPDATE("개인 채널은 수정할 수 없습니다."),

  // Message 관련 에러 코드
  MESSAGE_NOT_FOUND("메세지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

  // ReadStatus 관련 에러 코드
  READ_STATUS_NOT_FOUND("메세지 읽음 상태를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  DUPLICATE_READ_STATUS("이미 존재하는 메세지 읽음 상태입니다.", HttpStatus.CONFLICT),

  // Server 에러 코드
  INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  INVALID_REQUEST("잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
  ;

  private final String message;
  private final HttpStatus status;

  ErrorCode(String message) {
    this.message = message;
    this.status = HttpStatus.BAD_REQUEST;
  }
}
