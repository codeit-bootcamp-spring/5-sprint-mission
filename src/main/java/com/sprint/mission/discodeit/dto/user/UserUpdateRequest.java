package com.sprint.mission.discodeit.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserUpdateRequest {
    private final UUID id;
    private final String newUsername;
    private final String newEmail;
    private final String newPassword;
    private final UUID newProfileId; // 사용 안 하면 유지만
}
