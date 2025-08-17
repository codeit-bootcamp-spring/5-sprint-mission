package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;

public record UserUpdateAccountStatusCommand(

        @NotNull
        UUID userId,
        Optional<Boolean> verified,
        Optional<Boolean> banned,
        Optional<Boolean> deactivated
) {
}
