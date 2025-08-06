package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record UserUpdateRequest(
        UUID userId,
        String username,
        String email,
        String password,
        boolean updateProfileImage,
        UUID profileId
) {
}
