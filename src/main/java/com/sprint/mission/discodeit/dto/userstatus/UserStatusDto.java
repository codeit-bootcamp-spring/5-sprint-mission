package com.sprint.mission.discodeit.dto.userstatus;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(
    example = """
        {
          "id": "a7b3c4d5-e6f7-8901-a2b3-c4d5e6f78901",
          "userId": "dd210d1a-ebe6-499f-8936-859790fd3716",
          "lastActiveAt": "2025-09-04T09:40:04.880177Z"
        }
        """
)
public record UserStatusDto(
    UUID id,
    UUID userId,
    Instant lastActiveAt
) {
}
