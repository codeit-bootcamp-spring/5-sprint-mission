package com.sprint.mission.discodeit.dto.request;

public record UserCreateRequest(
        String username,
        String nickname,
        String email,
        String password
) {
}