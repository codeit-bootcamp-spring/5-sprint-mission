package com.sprint.mission.discodeit.dto.request;

import java.util.Optional;
import java.util.UUID;

public record UserUpdateUsernameCommand(
        UUID userId,
        Optional<String> username,
        Optional<String> password
) {
}
