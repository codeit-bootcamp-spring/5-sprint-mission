package com.sprint.mission.discodeit.dto.readstatus.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(
    example = """
        {
          "newLastReadAt": "2025-09-04T09:50:12.123456Z",
          "newNotificationEnabled": false
        }
        """
)
public record ReadStatusUpdateRequest(
    Instant newLastReadAt,
    Boolean newNotificationEnabled
) {
}
