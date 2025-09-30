package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  //Auth 관련 에러 코드
  AUTH_WRONG_PASSWORD("비밀번호가 일치하지 않습니다."),

  //BinaryContent 관련 에러 코드
  BINARY_CONTENT_NOT_FOUND("파일을 찾을 수없습니다."),
  BINARY_CONTENT_ALREADY_EXIST("이미 존재하는 파일입니다"),

  // Channel 관련 에러 코드
  CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
  PRIVATE_CHANNEL_UPDATE("채널 정보를 변경할 수 없습니다. 업데이트할 권한이 있는지 확인해 주세요."),

  // MESSAGE 관련 에러 코드
  MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다."),

  // READSTATUS 관련 에러 코드
  READSTATUS_ALREADY_EXIST("이미 존재하는 상태입니다."),
  READSTATUS_NOT_FOUND("상태를 찾을 수 없습니다 "),


  // User 관련 에러 코드
  USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
  DUPLICATE_USER("이미 존재하는 사용자입니다."),

  //USERSTATUS 관련 에러 코드
  USERSTATUS_ALREADY_EXIST("이미 존재하는 사용자 상태입니다."),
  USERSTATUS_NOT_FOUND("사용자의 상태를 찾을 수 없습니다."),


  // Server 에러 코드
  INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다."),
  INVALID_REQUEST("잘못된 요청입니다.");

  private final String message;
}

