package com.sprint.mission.discodeit.domain.enums;

import lombok.Getter;

@Getter
public enum UserStatusType {
  ONLINE("온라인"),
  DO_NOT_DISTURB("방해 금지"),
  IDLE("자리 비움"),
  OFFLINE("오프라인 표시");

  private final String displayName;

  UserStatusType(String displayName) {
    this.displayName = displayName;
  }
}
