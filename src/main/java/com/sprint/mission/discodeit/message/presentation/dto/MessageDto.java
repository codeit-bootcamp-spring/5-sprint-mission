package com.sprint.mission.discodeit.message.presentation.dto;

import com.sprint.mission.discodeit.binarycontent.presentation.dto.BinaryContentDto;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(
    example = """
        {
          "id": "e547b966-51cb-4e5a-9df0-f8996da38839",
          "createdAt": "2025-09-04T09:40:04.880177Z",
          "updatedAt": "2025-09-04T09:40:04.880177Z",
          "content": "Hello, world!",
          "channelId": "cce7f6a2-f709-4d43-a234-b18c5f43b662",
          "author": {
            "id": "0d5d2d3e-b3d8-48b3-b880-3711bd8c520f",
            "username": "test",
            "email": "test@example.com",
            "profile": null,
            "online": true,
            "role": "USER"
          },
          "attachments": [
            {
              "id": "d4c8c572-70c7-46cd-9cc8-403730dc62d4",
              "fileName": "attachment.png",
              "size": 14123,
              "contentType": "image/png"
            }
          ]
        }
        """
)
public record MessageDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String content,
    UUID channelId,
    UserDto author,
    List<BinaryContentDto> attachments
) {
}
