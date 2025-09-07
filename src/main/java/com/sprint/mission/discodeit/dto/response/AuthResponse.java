package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

public record AuthResponse(
    UUID userId,
    String username
) {}
