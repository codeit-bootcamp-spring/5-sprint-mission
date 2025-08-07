package com.sprint.mission.discodeit.exception;

public class UserStatusNotFoundException extends RuntimeException {
  public UserStatusNotFoundException() {
    super("사용자 상태를 찾을 수 없습니다.");
  }
}
