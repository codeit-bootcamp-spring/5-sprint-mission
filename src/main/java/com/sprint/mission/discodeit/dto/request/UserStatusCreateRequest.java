package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "UserStatusCreateRequest", description = "User 상태 생성 정보")
public record UserStatusCreateRequest(
        @Schema(description = "User ID", format = "uuid")
        UUID userId,
        @Schema(description = "마지막 활동 시각", format = "date-time")
        Instant lastActiveAt
) {
}
