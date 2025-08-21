package com.sprint.mission.discodeit.dto.request.auth;

import static com.sprint.mission.discodeit.support.Constants.MAX_EMAIL_LENGTH;
import static com.sprint.mission.discodeit.support.Constants.MAX_PASSWORD_LENGTH;
import static com.sprint.mission.discodeit.support.Constants.MIN_EMAIL_LENGTH;
import static com.sprint.mission.discodeit.support.Constants.MIN_PASSWORD_LENGTH;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthLoginRequest(

    @NotBlank
    @Email
    @Size(min = MIN_EMAIL_LENGTH, max = MAX_EMAIL_LENGTH)
    String email,

    @NotBlank
    @Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH)
    String password
) {

}
