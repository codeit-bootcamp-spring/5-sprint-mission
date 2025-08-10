package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.domain.enums.user.Status;

import java.util.UUID;

public record UserStatusResponse(
        UUID userId,
        Status status
) {
}
