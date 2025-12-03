package com.sprint.mission.discodeit.dto.jwt.data;

import com.sprint.mission.discodeit.dto.auth.data.UserDetailsDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    example = """
        {
          "userDetailsDto": {
            "id": "0d5d2d3e-b3d8-48b3-b880-3711bd8c520f",
            "username": "test",
            "role": "USER"
          },
          "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZX",
          "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZX"
        }
        """
)
public record JwtInformation(
    UserDetailsDto userDetailsDto,
    String accessToken,
    String refreshToken
) {
}
