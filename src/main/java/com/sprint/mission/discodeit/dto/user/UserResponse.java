package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;
import lombok.Builder;
import org.springframework.lang.Nullable;

import java.util.UUID;

public class UserResponse {

    @Builder
    public record detail(
            UUID id,
            String username,
            String email,
            @Nullable UUID profileId,
            @Nullable String imageUrl,
            String createdAt,
            String updatedAt,
            Boolean online
    ) {
        public static detail from (User user, boolean online) {
            return detail.builder()
                    .id(user.getId())
                    .username(user.getName())
                    .email(user.getEmail())
                    .profileId(user.getProfileId())
                    .imageUrl(user.getProfileId() != null ? "/api/binaryContents/" + user.getProfileId() : null)
                    .createdAt(user.getCreatedAtFormatted())
                    .updatedAt(user.getUpdatedAtFormatted())
                    .online(online)
                    .build();
        }
    }

    @Builder
    public record summary(
            UUID id,
            String username,
            String email,
            @Nullable UUID profileId,
            @Nullable String imageUrl,
            Boolean online
    ) {
        public static summary from (User user, boolean online) {
            return summary.builder()
                    .id(user.getId())
                    .username(user.getName())
                    .email(user.getEmail())
                    .profileId(user.getProfileId())
                    .imageUrl(user.getProfileId() != null ? "/api/binaryContents/" + user.getProfileId() : null)
                    .online(online)
                    .build();
        }
    }
}
