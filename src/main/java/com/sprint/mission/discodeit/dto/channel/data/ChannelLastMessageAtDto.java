package com.sprint.mission.discodeit.dto.channel.data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(
    example = """
        {
          "channelId": "cce7f6a2-f709-4d43-a234-b18c5f43b662",
          "lastMessageAt": "2025-09-04T09:40:04.880177Z"
        }
        """
)
public record ChannelLastMessageAtDto(
    UUID channelId,
    Instant lastMessageAt
) {
}
