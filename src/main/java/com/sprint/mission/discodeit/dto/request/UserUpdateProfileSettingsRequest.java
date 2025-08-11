package com.sprint.mission.discodeit.dto.request;

import java.util.Optional;

public record UserUpdateProfileSettingsRequest(
        Optional<String> globalName,
        Optional<String> bio
) {
}
