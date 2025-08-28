package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.Email;

public record UserUpdateRequest(

    String newUsername,

    @Email
    String newEmail,

    String newPassword
) {

}
