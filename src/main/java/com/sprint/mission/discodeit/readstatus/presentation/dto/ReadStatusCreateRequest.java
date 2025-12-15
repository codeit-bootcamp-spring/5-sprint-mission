package com.sprint.mission.discodeit.readstatus.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

@Schema(
    example = """
        {
          "channelId": "cce7f6a2-f709-4d43-a234-b18c5f43b662",
          "lastReadAt": "2025-09-04T09:40:04.880177Z"
        }
        """
)
public record ReadStatusCreateRequest(
    @NotNull UUID channelId,
    @NotNull Instant lastReadAt
) {
}
