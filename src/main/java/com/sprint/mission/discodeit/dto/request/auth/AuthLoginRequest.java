package com.sprint.mission.discodeit.dto.request.auth;

import static com.sprint.mission.discodeit.support.Constants.MAX_PASSWORD_LENGTH;
import static com.sprint.mission.discodeit.support.Constants.MAX_USERNAME_LENGTH;
import static com.sprint.mission.discodeit.support.Constants.MIN_PASSWORD_LENGTH;
import static com.sprint.mission.discodeit.support.Constants.MIN_USERNAME_LENGTH;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthLoginRequest(

    @NotBlank
    @Size(min = MIN_USERNAME_LENGTH, max = MAX_USERNAME_LENGTH)
    String username,

    @NotBlank
    @Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH)
    String password
) {

}
