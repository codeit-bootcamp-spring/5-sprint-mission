package com.sprint.mission.discodeit.dto.request;

public record UserUpdateRequest(
        String newUsername,
        String newNickname,
        String newEmail,
        String newPassword
) {
}
