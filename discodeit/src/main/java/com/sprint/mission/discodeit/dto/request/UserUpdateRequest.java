package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record UserUpdateRequest(
        UUID   Id,
        String username,
        String email,
        String password

) {
}
