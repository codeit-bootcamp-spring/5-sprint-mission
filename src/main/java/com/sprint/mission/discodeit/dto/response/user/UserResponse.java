package com.sprint.mission.discodeit.dto.response.user;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.entity.Role;
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
    private Role role;
    private Instant createdAt;
    private Instant updatedAt;
    @Nullable
    private BinaryContentDTO profile;
    boolean online;

    private UserResponse(User user) {
        this.id = user.getId();
        this.nickname = user.getDefaultNickname();
        this.email = user.getEmail();
        this.role = user.getRole();
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

    public static UserResponse auth(User user) {
        BinaryContentDTO profileDto = null;

        if (user.getProfile() != null) {
            profileDto = BinaryContentDTO.builder()
                    .id(user.getProfile().getId())
                    .build();
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getDefaultNickname())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .profile(profileDto)
                .online(false)
                .build();
    }

}
