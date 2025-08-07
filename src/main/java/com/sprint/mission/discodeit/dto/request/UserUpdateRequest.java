package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserUpdateRequest {
    private UUID userId;
    private String newUsername;
    private String newEmail;
    private byte[] newProfileImage; // optional

    public boolean hasNewProfileImage() {
        return newProfileImage != null && newProfileImage.length > 0;
    }
}
