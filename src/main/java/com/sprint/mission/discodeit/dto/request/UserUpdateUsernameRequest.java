package com.sprint.mission.discodeit.dto.request;

import java.util.Optional;

public record UserUpdateUsernameRequest(
        Optional<String> username
) {
}
