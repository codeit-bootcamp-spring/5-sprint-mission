package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
  DUPLICATE_USER("이미 존재하는 사용자입니다."),
  DUPLICATE_CHANNEL("이미 존재하는 채널입니다."),
  CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
  PRIVATE_CHANNEL_UPDATE("Private 채널은 수정할 수 없습니다."),
  MESSAGE_NOT_FOUND("메세지를 찾을 수 없습니다."),
  BINARYCONTENT_NOT_FOUND("첨부파일 ID를 찾을 수 없습니다."),
  WRONG_PASSWORD("비밀번호가 일치하지않습니다.");

  private final String message;
}
