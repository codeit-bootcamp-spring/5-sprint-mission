package com.sprint.mission.discodeit.dto.response.userstatus;

import java.util.UUID;

public record UserStatusResponse(
        UUID userId,
        String displayName
) {
}
