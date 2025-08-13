package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UpdateUserRequest(
        UUID userId,
        String email,
        String userName,
        String nickname,
        String password,
        String phoneNumber
) {}
