package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.domain.enums.user.Status;

import java.util.UUID;

public record UserStatusUpdateCommand(
        UUID id,
        Status status,
        Boolean login,
        Boolean logout,
        Boolean heartbeat,
        Boolean unfix
) {
}
