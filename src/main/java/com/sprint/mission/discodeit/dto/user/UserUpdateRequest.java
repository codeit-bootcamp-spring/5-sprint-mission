package com.sprint.mission.discodeit.dto.user;

import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString
public class UserUpdateRequest {
    private final UUID id;
    private final String userId;
    private final String email;
    private final String password;
    private ProfileImageRequest profileImage;

    public UserUpdateRequest(UUID id, String userId, String email, String password, ProfileImageRequest profileImage) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }

    public UserUpdateRequest(UUID id, String userId, String email, String password) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.password = password;
    }
}
