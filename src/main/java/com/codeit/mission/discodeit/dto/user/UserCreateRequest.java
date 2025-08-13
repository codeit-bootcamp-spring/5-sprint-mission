package com.codeit.mission.discodeit.dto.user;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserCreateRequest {

    private final String username;
    private final String email;
    private final String password;
    private final ProfileImageRequest profileImage;

    public UserCreateRequest(String username, String email, String password, ProfileImageRequest profileImage) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }

    public UserCreateRequest(String username, String email, String password) {
        this(username, email, password, null);
    }
}
