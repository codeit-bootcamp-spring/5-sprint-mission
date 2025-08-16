package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.NotBlank;

import java.util.Optional;
import java.util.UUID;

public record UserUpdateAccountStatusCommand(

        @NotBlank
        UUID userId,
        Optional<Boolean> verified,
        Optional<Boolean> banned,
        Optional<Boolean> deactivated
) {
}
