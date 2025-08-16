package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserUpdateProfileImageRequest(
        @NotNull UUID profileId
) {
}
