package com.sprint.mission.discodeit.dto.request;

import java.util.Optional;
import java.util.UUID;

public record UserUpdateProfileSettingsCommand(
        UUID userId,
        Optional<Boolean> subscribedToNewsletter,
        Optional<String> globalName,
        Optional<String> bio,
        Optional<UUID> profiled
) {
}
