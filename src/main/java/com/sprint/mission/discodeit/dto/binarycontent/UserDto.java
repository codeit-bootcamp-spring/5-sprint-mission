package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.User;

import java.time.Instant;
import java.util.UUID;


/* 사용자 전체 목록 조회용
 * findALl 메서드를 위해 */

public record UserDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        UUID profileId,
        Boolean online
) {
    public static UserDto fromEntity(User user, boolean online) {
        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUserId(),
                user.getEmail(),
                user.getProfileId(),
                online
        );
    }
}
