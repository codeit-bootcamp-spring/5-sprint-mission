package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.domain.enums.user.Status;

import java.util.UUID;

public record UserStatusUpdateByUserIdCommand(
        UUID userId,
        Status status,
        Boolean login,
        Boolean logout,
        Boolean heartbeat,
        Boolean unfix
) {
}
