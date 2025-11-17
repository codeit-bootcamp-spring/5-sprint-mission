package com.sprint.mission.discodeit.domain.dto.user;

import java.util.UUID;

public record UserSessionStatus(
  UUID userID,
  long lastRequest,
  boolean online
) {
}

