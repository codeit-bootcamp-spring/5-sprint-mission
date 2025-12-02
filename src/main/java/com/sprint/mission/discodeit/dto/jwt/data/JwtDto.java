package com.sprint.mission.discodeit.dto.jwt.data;

import com.sprint.mission.discodeit.dto.user.data.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    example = """
        {
          "userDto": {
            "id": "0d5d2d3e-b3d8-48b3-b880-3711bd8c520f",
            "username": "test",
            "email": "test@example.com",
            "profile": null,
            "online": true,
            "role": "USER"
          },
          "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZX"
        }
        """
)
public record JwtDto(
    UserDto userDto,
    String accessToken
) {
}
