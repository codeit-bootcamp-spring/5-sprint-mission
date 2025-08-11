package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record UserStatusUpdateRequest(
        UUID id,
        boolean loginStatus
) {
}
