package com.sprint.mission.discodeit.domain.user.dto;

import java.time.Instant;

public record UserStatusUpdateRequest(
    Instant newLastActiveAt
) {

}
