package com.sprint.mission.discodeit.dto.request.user;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserUpdateRequest {
    @Nullable
    private String newUsername;
    @Nullable
    private String newEmail;
    @Nullable
    private String newPassword;
}