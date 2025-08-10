package com.sprint.mission.discodeit.dto.request;

import java.util.Optional;
import java.util.UUID;

public record UserUpdateAccountStatusCommand(
        UUID userId,
        Optional<Boolean> verified,
        Optional<Boolean> banned,
        Optional<Boolean> deactivated
) {
}
