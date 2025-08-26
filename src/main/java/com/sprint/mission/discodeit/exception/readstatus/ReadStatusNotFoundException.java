package com.sprint.mission.discodeit.exception.readstatus;

public class ReadStatusNotFoundException extends RuntimeException {
  public ReadStatusNotFoundException() {
    super("읽음 상태를 찾을 수 없습니다.");
  }
}
