package com.sprint.mission.discodeit.dto.request;

import java.util.Optional;
import java.util.UUID;

public record UserUpdatePhoneNumberCommand(
        UUID userId,
        Optional<String> phoneNumber
) {
}
