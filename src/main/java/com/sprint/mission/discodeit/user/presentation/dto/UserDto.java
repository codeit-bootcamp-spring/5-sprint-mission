package com.sprint.mission.discodeit.user.presentation.dto;

import com.sprint.mission.discodeit.binarycontent.presentation.dto.BinaryContentDto;
import com.sprint.mission.discodeit.user.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(
    example = """
        {
          "id": "dd210d1a-ebe6-499f-8936-859790fd3716",
          "username": "test",
          "email": "test@example.com",
          "profile": {
            "id": "957a0ce6-8fde-4397-bb9a-446dcb49578e",
            "fileName": "profile.png",
            "size": 12529,
            "contentType": "image/png"
          },
          "online": true,
          "role": "USER"
        }
        """
)
public record UserDto(
    UUID id,
    String username,
    String email,
    BinaryContentDto profile,
    boolean online,
    Role role
) {
}
