package com.sprint.mission.discodeit.dto.response.user;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.entity.User;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserResponse {
    private UUID id;
    private String username; // loginId 임
    private String nickname; // defaultNickname 임
    private String email;
    private Instant createdAt;
    private Instant updatedAt;
    @Nullable
    private BinaryContentDTO profile;
    boolean online;

    private UserResponse(User user) {
        this.id = user.getId();
        this.nickname = user.getDefaultNickname();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        if (user.getProfile() != null) {
            this.profile = BinaryContentDTO.builder()
                    .id(user.getProfile().getId())
                    .fileName(user.getProfile().getFileName())
                    .contentType(user.getProfile().getContentType())
                    .size(user.getProfile().getSize())
                    .build();
        }
        this.username = user.getUsername(); // loginId는 username으로
    }

    public static UserResponse success(User user) {
        return new UserResponse(user);
    }
}
