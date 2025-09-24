package com.sprint.mission.discodeit.exception;

public enum ErrorCode {

  /* 에러 이름과 메세지를 한 곳에 enum으로 모아놓음
   */

  // Auth 관련
  //INVALID_LOGIN("아이디 또는 비밀번호가 올바르지 않습니다."),
  //TOKEN_EXPIRED("로그인 토큰이 만료되었습니다."),
  //INVALID_TOKEN("유효하지 않은 토큰입니다."),
  //NO_AUTHORITY("접근 권한이 없습니다."),

  // User 관련
  USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
  DUPLICATE_USER("이미 존재하는 사용자입니다."),
  INVALID_USER_STATUS("유효하지 않은 사용자 상태입니다."),
  USER_EMAIL_ALREADY_EXISTS("이미 가입된 이메일입니다."),
  INVALID_PASSWORD("비밀번호가 올바르지 않습니다."),
  INVALID_EMAIL("이메일 형식이 올바르지 않습니다."),

  // Channel 관련
  CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
  DUPLICATE_CHANNEL("이미 존재하는 채널입니다."),
  PRIVATE_CHANNEL_UPDATE("비공개 채널은 수정할 수 없습니다."),
  NOT_CHANNEL_OWNER("채널 소유자가 아닙니다."),
  NOT_CHANNEL_PARTICIPANT("해당 채널 참가자가 아닙니다."),

  // Message 관련
  MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다."),
  MESSAGE_SEND_FORBIDDEN("메시지 전송 권한이 없습니다."),
  ATTACHMENT_NOT_FOUND("첨부파일을 찾을 수 없습니다."),
  MESSAGE_DELETE_FORBIDDEN("메시지 삭제 권한이 없습니다."),

  // BinaryContent (첨부파일)
  FILE_NOT_FOUND("파일을 찾을 수 없습니다."),
  FILE_UPLOAD_FAILED("파일 업로드에 실패했습니다."),
  FILE_DOWNLOAD_FAILED("파일 다운로드에 실패했습니다."),
  UNSUPPORTED_FILE_TYPE("지원하지 않는 파일 형식입니다."),

  // ReadStatus
  READ_STATUS_NOT_FOUND("읽음 상태 정보를 찾을 수 없습니다."),
  INVALID_READ_STATUS("유효하지 않은 읽음 상태입니다."),

  // UserStatus
  USER_STATUS_NOT_FOUND("사용자 상태 정보를 찾을 수 없습니다."),

  // Validation 공통
  INVALID_REQUEST("요청 값이 올바르지 않습니다."),
  UNAUTHORIZED("인증이 필요합니다."),
  FORBIDDEN("금지된 요청입니다."),
  INTERNAL_ERROR("서버 내부 오류가 발생했습니다."),
  ;


  private final String message; //에러 메세지 저장용

  ErrorCode(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
