package com.sprint.mission.discodeit.dto.data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "UserDto")
public record UserDto(
        @Schema(description = "User ID", format = "uuid")
        UUID id,
        @Schema(description = "생성 시각", format = "date-time")
        Instant createdAt,
        @Schema(description = "수정 시각", format = "date-time")
        Instant updatedAt,
        @Schema(description = "사용자명")
        String username,
        @Schema(description = "이메일")
        String email,
        @Schema(description = "프로필 BinaryContent ID", format = "uuid")
        UUID profileId,
        @Schema(description = "온라인 여부")
        Boolean online
) {
}
