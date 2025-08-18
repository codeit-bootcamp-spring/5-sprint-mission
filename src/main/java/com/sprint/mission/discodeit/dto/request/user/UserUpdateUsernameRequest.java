package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateUsernameRequest(

    @NotBlank
    @Size(min = 2, max = 32)
    @Pattern(
        regexp = "^(?!.*\\.\\.)[a-z0-9._]+$",
        flags = Pattern.Flag.CASE_INSENSITIVE
    )
    String username
) {

}
