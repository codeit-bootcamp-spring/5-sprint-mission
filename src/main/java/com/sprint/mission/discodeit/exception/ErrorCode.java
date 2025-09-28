package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  INTERNAL_ERROR("서버 내부 오류가 발생했습니다."),
  INVALID_ARGUMENT("잘못된 요청 값입니다."),
  NOT_FOUND("리소스를 찾을 수 없습니다."),

  USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
  USER_ALREADY_EXISTS("이미 존재하는 사용자입니다."),

  CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
  CHANNEL_PRIVATE_ACCESS_DENIED("PRIVATE 채널 접근이 거부되었습니다."),

  MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다."),

  BINARY_CONTENT_NOT_FOUND("파일 메타 정보를 찾을 수 없습니다."),
  FILE_STORAGE_FAILED("파일 저장 중 오류가 발생했습니다."),

  READ_STATUS_NOT_FOUND("읽음 상태를 찾을 수 없습니다."),
  READ_STATUS_ALREADY_EXISTS("읽음 상태가 이미 존재합니다."),

  USER_STATUS_NOT_FOUND("유저 상태를 찾을 수 없습니다."),
  USER_STATUS_ALREADY_EXISTS("유저 상태가 이미 존재합니다.");

  private final String message;

  ErrorCode(String message) {
    this.message = message;
  }
}
