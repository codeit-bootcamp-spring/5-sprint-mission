package com.sprint.mission.discodeit.dto.userstatus;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.UUID;

public record UserStatusDto(
    UUID id,
    UUID userId,
    Instant lastActiveAt
) {

    public static UserStatusDto from(UserStatus us) {
        return new UserStatusDto(
            us.getId(),
            us.getUser().getId(),
            us.getLastActiveAt()
        );
    }
}
