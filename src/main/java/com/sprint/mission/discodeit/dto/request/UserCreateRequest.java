package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

@Getter
public class UserCreateRequest {
    private String username;
    private String email;
    private String password;
    private byte[] profileImage; // optional

    public boolean hasProfileImage() {
        return profileImage != null && profileImage.length > 0;
    }
}
