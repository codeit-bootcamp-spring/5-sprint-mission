package com.sprint.mission.discodeit.dto.request;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public record UserUpdateCommand(
        UUID userId,
        Optional<String> email,
        Optional<String> username,
        Optional<String> password,
        Optional<LocalDate> birthDate,
        Optional<Boolean> subscribedToNewsletter,
        Optional<String> globalName,
        Optional<String> phoneNumber,
        Optional<String> bio,
        Optional<String> avatar,
        Optional<Boolean> verified,
        Optional<Boolean> banned,
        Optional<Boolean> deactivated
) {
}
