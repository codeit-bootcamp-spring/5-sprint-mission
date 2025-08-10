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
        Optional<ProfileImageCommand> newProfileImage, // 선택: 교체
        Optional<Boolean> removeProfileImage           // 선택: 제거 플래그
) {
}
