package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record UserCreateRequest(
        String username,
        String email,
        String password,
        boolean uploadProfileImage,
        UUID profileId
) {
}
