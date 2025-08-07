package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserUpdateRequest {
    private UUID userId;
    private String newUsername;
    private String newEmail;
    private String newPassword;
    private byte[] newProfileImage;

    public UUID getUserId() {
        return userId;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public byte[] getNewProfileImage() {
        return newProfileImage;
    }

    public boolean hasNewProfileImage() {
        return newProfileImage != null && newProfileImage.length > 0;
    }
}
