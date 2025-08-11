package com.sprint.mission.discodeit.dto.request;

import java.util.Optional;

public record UserUpdateEmailRequest(
        Optional<String> email
) {
}
