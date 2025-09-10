package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record UserUpdateRequest(
    @NotEmpty String newUsername,
    @NotEmpty @Email String newEmail,
    @NotEmpty String newPassword
) {

}
