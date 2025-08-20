package com.sprint.mission.discodeit.dto.user;

public record UserUpdateResponse(
        String newUsername,
        String newEmail,
        String newPassword
) {
}
