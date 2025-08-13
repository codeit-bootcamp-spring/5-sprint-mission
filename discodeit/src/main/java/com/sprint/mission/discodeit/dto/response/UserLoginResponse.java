package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

public record UserLoginResponse(
        UUID id,
        String username,
        String email

) {
}
