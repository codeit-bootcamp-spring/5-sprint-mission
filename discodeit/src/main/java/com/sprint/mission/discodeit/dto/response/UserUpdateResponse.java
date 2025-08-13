package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

public record UserUpdateResponse(
        UUID   Id,
        String username,
        String email,
        String password

) {
}
