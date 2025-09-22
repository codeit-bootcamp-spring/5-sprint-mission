package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  // 공통
  INVALID_REQUEST("잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
  INTERNAL_ERROR("내부 서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

  // Auth
  AUTH_INVALID_CREDENTIALS("아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),

  // User
  USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  DUPLICATE_USER("이미 존재하는 사용자입니다.", HttpStatus.CONFLICT),

  // UserStatus
  USER_STATUS_NOT_FOUND("사용자 상태를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  USER_STATUS_ALREADY_EXISTS("이미 사용자 상태가 존재합니다.", HttpStatus.CONFLICT),

  // Channel
  CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  PRIVATE_CHANNEL_UPDATE("PRIVATE 채널은 수정할 수 없습니다.", HttpStatus.FORBIDDEN),

  // Message
  MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

  // ReadStatus
  READ_STATUS_NOT_FOUND("읽음 상태를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  READ_STATUS_ALREADY_EXISTS("이미 읽음 상태가 존재합니다.", HttpStatus.CONFLICT),

  // File/BinaryContent
  BINARY_CONTENT_NOT_FOUND("파일을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

  private final String message;
  private final HttpStatus status;

  ErrorCode(String message, HttpStatus status) {
    this.message = message;
    this.status = status;
  }
}
