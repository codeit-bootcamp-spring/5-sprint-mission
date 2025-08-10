package com.sprint.mission.discodeit.dto.request;

import java.util.Optional;
import java.util.UUID;

public record UserUpdateEmailCommand(
        UUID userId,
        Optional<String> email
) {
}
