package com.sprint.mission.discodeit.readstatus.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(
    example = """
        {
          "id": "bc482f77-d3a9-43fd-a272-4da85df4f041",
          "userId": "dd210d1a-ebe6-499f-8936-859790fd3716",
          "channelId": "cce7f6a2-f709-4d43-a234-b18c5f43b662",
          "lastReadAt": "2025-09-04T09:40:04.880177Z",
          "notificationEnabled": true
        }
        """
)
public record ReadStatusDto(
    UUID id,
    UUID userId,
    UUID channelId,
    Instant lastReadAt,
    boolean notificationEnabled
) {
}
