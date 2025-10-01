package com.sprint.mission.discodeit.dto.request.user;

import com.sprint.mission.discodeit.dto.request.binaryContent.UserProfileImageRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserCreateRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String defaultNickname;
    @NotBlank
    @Email
    private String email;
    @Nullable
    private UserProfileImageRequest profileImage;

    public User toUser() {
        return toUserWithProfile(null);
    }

    public User toUserWithProfile(BinaryContent profile) {
        String nickname = (defaultNickname != null) ? defaultNickname : username;
        return new User(username, password, nickname, email, profile);
    }

}
