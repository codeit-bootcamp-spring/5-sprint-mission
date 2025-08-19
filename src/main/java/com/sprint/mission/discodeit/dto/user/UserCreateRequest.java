package com.sprint.mission.discodeit.dto.user;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class UserCreateRequest {

    private final String userId;
    private final String email;
    private final String password;
    private ProfileImageRequest profileImage;

    public UserCreateRequest(String userId, String email, String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
    }

    public UserCreateRequest(String userId, String email, String password, ProfileImageRequest profileImage) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }
}
