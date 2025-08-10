package com.sprint.mission.discodeit.dto.request;

public record AuthLoginCommand(
        String email,
        String password
) {
}
