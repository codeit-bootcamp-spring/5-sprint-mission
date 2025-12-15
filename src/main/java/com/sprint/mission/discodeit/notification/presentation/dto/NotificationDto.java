package com.sprint.mission.discodeit.notification.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(
    example = """
        {
          "id": "0d5d2d3e-b3d8-48b3-b880-3711bd8c520f",
          "createdAt": "2025-09-04T09:40:04.880177Z",
          "receiverId": "957a0ce6-8fde-4397-bb9a-446dcb49578e",
          "title": "보낸 사람 (#채널명)",
          "content": "메시지 내용"
        }
        """
)
public record NotificationDto(
    UUID id,
    Instant createdAt,
    UUID receiverId,
    String title,
    String content
) {
}
