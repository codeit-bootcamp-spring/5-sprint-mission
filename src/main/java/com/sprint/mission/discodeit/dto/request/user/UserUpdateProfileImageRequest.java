package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UserUpdateProfileImageRequest(

        @NotBlank
        UUID profileId
) {
}
