package com.sprint.mission.discodeit.dto.user;

public record UserCreateResponse(
        String username,
        String email,
        String password
) {
}
