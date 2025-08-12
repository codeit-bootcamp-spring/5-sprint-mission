package com.sprint.mission.discodeit.service.dto.user;

import java.util.Optional;

/** create용 요청 DTO */
public class CreateUserRequest {
    public String username;
    public String email;
    public String password;
    public Optional<ProfileImageUpload> profileImage; // 선택

    public CreateUserRequest(String username, String email, String password,
                             Optional<ProfileImageUpload> profileImage) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage == null ? Optional.empty() : profileImage;
    }
}
