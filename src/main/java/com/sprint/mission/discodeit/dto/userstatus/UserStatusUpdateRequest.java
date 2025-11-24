package com.sprint.mission.discodeit.dto.userstatus;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(
    example = """
        {
          "newLastActiveAt": "2025-09-04T09:50:12.123456Z"
        }
        """
)
public record UserStatusUpdateRequest(
    Instant newLastActiveAt
) {
}
