package com.codeit.mission.discodeit.dto.user;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class UserUpdateRequest {

    private final UUID id;

    private final String username;
    private final String email;
    private final String password;

    private final ProfileImageRequest profileImage;

    public UserUpdateRequest(UUID id, String username, String email, String password, ProfileImageRequest profileImage) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }

    public UserUpdateRequest(UUID userId, String username, String email, String password) {
        this(userId, username, email, password, null);
    }
}
