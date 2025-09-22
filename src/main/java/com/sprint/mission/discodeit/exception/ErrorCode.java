package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  USER_NOT_FOUND("사용자를 찾을 수 없습니다.", 404), DUPLICATE_USER("이미 존재하는 사용자입니다.", 409), CHANNEL_NOT_FOUND(
      "채널을 찾을 수 없습니다.", 404), PRIVATE_CHANNEL_UPDATE("비공개 채널은 수정할 수 없습니다.",
      403), INVALID_USER_STATUS("유효하지 않은 유저 상태입니다.", 403), MESSAGE_NOT_FOUND("메세지를 찾을 수 없습니다.",
      404), BINARY_CONTENT_NOT_FOUND("파일을 찾을 수 없습니다.", 404), BINARY_CONTENT_NOT_CREATE(
      "파일을 생성할 수 없습니다.", 400), LOGIN_FAIL("로그인 실패", 403), READ_STATUS_NOT_FOUND("읽기 상태를 찾을 수 없습니다.",
      404), READ_STATUS_EXISTS("읽기 상태가 이미 존재합니다.", 409), USER_STATUS_NOT_FOUND("유저 상태를 찾을 수 없습니다.",
      404), USER_STATUS_EXISTS("유저 상태가 이미 존재합니다.", 409),
  VALIDATION_ERROR("입력값 검증에 실패했습니다.", 400),
  ;
  private final String message;
  private final int status;

  ErrorCode(String message, int status) {
    this.message = message;
    this.status = status;
  }
}
