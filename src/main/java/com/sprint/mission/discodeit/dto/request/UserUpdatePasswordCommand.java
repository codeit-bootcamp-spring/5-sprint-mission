package com.sprint.mission.discodeit.dto.request;

import java.util.Optional;
import java.util.UUID;

public record UserUpdatePasswordCommand(
        UUID userId,
        Optional<String> oldPassword,
        Optional<String> newPassword
) {
}
