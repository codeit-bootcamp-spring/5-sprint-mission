package com.sprint.mission.discodeit.dto.request.user;

public record UserCreateRequest(
        String username,
        String nickname,
        String email,
        String password
) {
}