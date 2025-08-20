package com.sprint.mission.discodeit.dto.user;

public record CreateUserRequest(
        String email,
        String userName,
        String nickname,
        String password,
        String phoneNumber
) {}