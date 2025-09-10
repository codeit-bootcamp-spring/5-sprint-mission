package com.sprint.mission.discodeit.dto.userstatus;

import com.sprint.mission.discodeit.entity.sub.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusDto(
        UUID id,
        UUID userId,
        Instant lastActiveAt
) {}
