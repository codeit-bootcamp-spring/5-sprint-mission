package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        UUID profileId,
        String email,
        String userName,
        String nickname,
        String phoneNumber,
        boolean online,
        Instant lastActiveAt
) {}
