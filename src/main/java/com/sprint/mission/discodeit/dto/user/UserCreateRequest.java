package com.sprint.mission.discodeit.dto.user;

import lombok.Getter;

@Getter
public class UserCreateRequest {
    private final String username;
    private final String email;
    private final String password;

    private final byte[] profileImage;

    public UserCreateRequest(String username, String email, String password) {
        this(username, email, password, null);
    }

    public UserCreateRequest(String username, String email, String password, byte[] profileImage) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }

    public boolean hasProfileImage() {
        return profileImage != null && profileImage.length > 0;
    }

    public byte[] getNewProfileImage() {
        return profileImage;
    }
}
