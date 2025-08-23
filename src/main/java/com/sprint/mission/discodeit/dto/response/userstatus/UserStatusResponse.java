package com.sprint.mission.discodeit.dto.response.userstatus;

import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.enums.UserStatusType;
import java.time.Instant;
import java.util.UUID;

public record UserStatusResponse(

    UUID id,
    Instant createdAt,
    Instant updatedAt,
    UUID userId,
    Instant lastActiveAt,
    boolean online,
    UserStatusType userStatusType
) {

  public static UserStatusResponse from(UserStatus us) {
    return new UserStatusResponse(
        us.getId(),
        us.getCreatedAt(),
        us.getUpdatedAt(),
        us.getUserId(),
        us.getLastActiveAt(),
        !UserStatusType.OFFLINE.equals(us.getType()),
        us.getType()
    );
  }
}
