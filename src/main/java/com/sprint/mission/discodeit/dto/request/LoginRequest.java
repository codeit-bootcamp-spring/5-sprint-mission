package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(@NotBlank(message = "Username is mandatory") String username,

                           @NotBlank(message = "Password is mandatory") @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters") String password) {
}
