package com.sprint.mission.discodeit.dto.user;

public record CreateUserRequest(
        String email,
        String username,
        String nickname,
        String password,
        String phoneNumber
) {}