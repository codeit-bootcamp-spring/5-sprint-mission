package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

public class JwtDto {

  @Getter
  @Builder
  @Schema(name = "JwtDto")
  public static class JwtResponse {

    private UserDto.DetailResponse userDto;
    private String accessToken;

  }

  @Getter
  @Builder
  public static class JwtInformation {

    private final String accessToken;
    private final String refreshToken;
    private final Instant accessTokenExpiresAt;
    private final Instant refreshTokenExpiresAt;
  }
}
