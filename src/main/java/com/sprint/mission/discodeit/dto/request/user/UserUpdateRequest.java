package com.sprint.mission.discodeit.dto.request.user;

public record UserUpdateRequest(
        String newUsername,
        String newNickname,
        String newEmail,
        String newPassword
) {
}
