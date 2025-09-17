package com.sprint.mission.discodeit.domain.user.dto;

public record UserUpdateRequest(
    String newUsername,
    String newEmail,
    String newPassword
) {

}
