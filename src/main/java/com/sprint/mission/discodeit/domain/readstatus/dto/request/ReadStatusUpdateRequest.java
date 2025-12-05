package com.sprint.mission.discodeit.domain.readstatus.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(
    example = """
        {
          "newLastReadAt": null,
          "newNotificationEnabled": false
        }
        """
)
public record ReadStatusUpdateRequest(
    Instant newLastReadAt,
    Boolean newNotificationEnabled
) {
}
