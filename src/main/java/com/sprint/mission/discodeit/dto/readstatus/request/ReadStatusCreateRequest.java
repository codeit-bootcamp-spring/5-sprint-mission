package com.sprint.mission.discodeit.dto.readstatus.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

@Schema(
    example = """
        {
          "userId": "dd210d1a-ebe6-499f-8936-859790fd3716",
          "channelId": "cce7f6a2-f709-4d43-a234-b18c5f43b662",
          "lastReadAt": "2025-09-04T09:40:04.880177Z",
          "notificationEnabled": true
        }
        """
)
public record ReadStatusCreateRequest(
    @NotNull
    UUID userId,
    @NotNull
    UUID channelId,
    @NotNull
    Instant lastReadAt,
    boolean notificationEnabled
) {
}
