package com.sprint.mission.discodeit.service.dto.user;

import java.util.Optional;
import java.util.UUID;

/** update용 요청 DTO */
public class UpdateUserRequest {
    public UUID userId;                         // 수정 대상
    public String newUsername;                  // null이면 미변경
    public String newEmail;                     // null이면 미변경
    public String newPassword;                  // null이면 미변경
    public Optional<ProfileImageUpload> newProfileImage; // 선택(있으면 교체)
    public boolean removeProfileImage;          // true면 프로필 제거

    public UpdateUserRequest(UUID userId, String newUsername, String newEmail, String newPassword,
                             Optional<ProfileImageUpload> newProfileImage, boolean removeProfileImage) {
        this.userId = userId;
        this.newUsername = newUsername;
        this.newEmail = newEmail;
        this.newPassword = newPassword;
        this.newProfileImage = newProfileImage == null ? Optional.empty() : newProfileImage;
        this.removeProfileImage = removeProfileImage;
    }
}
