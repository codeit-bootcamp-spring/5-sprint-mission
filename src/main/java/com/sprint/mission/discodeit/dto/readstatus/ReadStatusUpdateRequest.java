package com.sprint.mission.discodeit.dto.readstatus;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(
    example = """
        {
          "newLastReadAt": "2025-09-04T09:50:12.123456Z"
        }
        """
)
public record ReadStatusUpdateRequest(
    Instant newLastReadAt
) {
}
