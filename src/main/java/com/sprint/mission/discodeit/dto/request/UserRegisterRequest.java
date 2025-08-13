package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UserRegisterRequest(
        @NotBlank @Email String email,
        @NotBlank String username,
        @NotBlank String password,
        @NotNull LocalDate birthDate,
        boolean subscribedToNewsletter,
        String globalName
) {
}
