package com.sprint.mission.discodeit.dto.auth;

import java.util.UUID;

public record LoginResponseDto(
        UUID id,
        String email,
        String name,
        boolean isOnline,
        String imageUrl
) {}
