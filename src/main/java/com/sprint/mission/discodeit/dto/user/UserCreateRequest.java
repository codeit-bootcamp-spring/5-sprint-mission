package com.sprint.mission.discodeit.dto.user;

import lombok.Getter;

@Getter
public class UserCreateRequest {
    private String username;
    private String email;
    private String password;
    private byte[] profileImage;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public boolean hasProfileImage() {
        return profileImage != null && profileImage.length > 0;
    }

    public byte[] getNewProfileImage() {
        return profileImage;
    }
}
