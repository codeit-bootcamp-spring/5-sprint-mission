package com.sprint.mission.discodeit.channel.presentation.dto;

import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(
    example = """
        {
          "id": "7e297daa-aeec-47ae-b1e0-c63f7a8f9824",
          "type": "PRIVATE",
          "name": null,
          "description": null,
          "participants": [
            {
              "id": "dd210d1a-ebe6-499f-8936-859790fd3716",
              "username": "test",
              "email": "test@example.com",
              "profile": null,
              "online": false,
              "role": "USER"
            },
            {
              "id": "8fb5dd71-b7a0-4b5d-bf37-ea410473c618",
              "username": "test2",
              "email": "test2@example.com",
              "profile": {
                "id": "3a44bc04-e179-4533-bcf1-cfdc3aa86a4a",
                "fileName": "profile2.webp",
                "size": 12529,
                "contentType": "image/webp"
              },
              "online": true,
              "role": "CHANNEL_MANAGER"
            }
          ],
          "lastMessageAt": null
        }
        """
)
public record ChannelDto(
    UUID id,
    ChannelType type,
    String name,
    String description,
    List<UserDto> participants,
    Instant lastMessageAt
) {
}
