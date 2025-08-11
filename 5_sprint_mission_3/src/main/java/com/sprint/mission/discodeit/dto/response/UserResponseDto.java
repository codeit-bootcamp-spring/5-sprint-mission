package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

public record UserResponseDto (
        UUID id,
        String username,
        String email,
        UUID profileImageId,
        boolean online
) {}