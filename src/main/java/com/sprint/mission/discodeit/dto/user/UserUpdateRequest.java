package com.sprint.mission.discodeit.dto.user;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserUpdateRequest {
    private UUID userId;
    private String newUsername;
    private String newEmail;
    private String newPassword;
    private byte[] newProfileImage;

    public boolean hasNewProfileImage() {
        return newProfileImage != null && newProfileImage.length > 0;
    }
}
