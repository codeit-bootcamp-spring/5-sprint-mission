package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record AddUserRequest(
        String userName,
        String email,
        String password,
        String phoneNumber,
        UUID profileId) {
}
