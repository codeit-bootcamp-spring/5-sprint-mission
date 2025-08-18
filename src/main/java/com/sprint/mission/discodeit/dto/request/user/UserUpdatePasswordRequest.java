package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdatePasswordRequest(

    @NotBlank
    @Size(min = 8, max = 72)
    String password
) {

}
