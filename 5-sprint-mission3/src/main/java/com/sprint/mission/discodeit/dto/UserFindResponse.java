package com.sprint.mission.discodeit.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserFindResponse(
        UUID id,
        String username,
        String email,
        UUID profileId,
        boolean isOnline
) {
}
