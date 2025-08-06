package com.sprint.mission.discodeit.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserFindResponse(
        UUID profileId,
        String username,
        String email,
        boolean loginStatus
) {
}
