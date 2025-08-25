package com.sprint.mission.discodeit.dto.request;

/** update용 요청 DTO */
public record UserUpdateRequest (
        String newUsername,
        String newEmail,
        String newPassword
) {
}
