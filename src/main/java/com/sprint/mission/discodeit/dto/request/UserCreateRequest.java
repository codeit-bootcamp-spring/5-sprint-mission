package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
    @NotBlank @Size(min = 2, max = 20) String username,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 6, max = 50) String password
) {}
