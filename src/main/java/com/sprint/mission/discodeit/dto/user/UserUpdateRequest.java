package com.sprint.mission.discodeit.dto.user;

import java.util.Optional;
import java.util.UUID;

/** update용 요청 DTO */
public record UserUpdateRequest (
        String newUsername,
        String newEmail,
        String newPassword
) {
}
