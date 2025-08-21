package com.sprint.mission.discodeit.dto.request.user;

import static com.sprint.mission.discodeit.support.Constants.MAX_EMAIL_LENGTH;
import static com.sprint.mission.discodeit.support.Constants.MAX_PASSWORD_LENGTH;
import static com.sprint.mission.discodeit.support.Constants.MAX_USERNAME_LENGTH;
import static com.sprint.mission.discodeit.support.Constants.MIN_EMAIL_LENGTH;
import static com.sprint.mission.discodeit.support.Constants.MIN_PASSWORD_LENGTH;
import static com.sprint.mission.discodeit.support.Constants.MIN_USERNAME_LENGTH;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

    @Size(min = MIN_USERNAME_LENGTH, max = MAX_USERNAME_LENGTH)
    @Pattern(regexp = "^(?!.*\\.\\.)[A-Za-z0-9._]+$")
    String newUsername,

    @Size(min = MIN_EMAIL_LENGTH, max = MAX_EMAIL_LENGTH)
    @Email
    String newEmail,

    @Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH)
    String newPassword
) {

}
