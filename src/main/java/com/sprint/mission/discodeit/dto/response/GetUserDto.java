package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

public record GetUserDto(
        UUID userId,
        String username,
        String email,
        String phoneNumber,
        UUID profileId,
        boolean isOnline
) {}
