package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @NotBlank @Size(min = 2, max = 20) String newUsername,
    @NotBlank @Email String newEmail,
    @NotBlank @Size(min = 6, max = 50) String newPassword
) {}
