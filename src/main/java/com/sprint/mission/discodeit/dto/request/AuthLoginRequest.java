package com.sprint.mission.discodeit.dto.request;

public record AuthLoginRequest(
        String email,
        String password
) {
}
