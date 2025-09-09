package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

public class UserStatusDto {

  @Getter
  @Builder
  public static class Create {

    private UUID userId;
  }

  @Getter
  @Builder
  @Schema(name = "UserStatusDetailResponse")
  public static class DetailResponse {

    private UUID id;
    private UUID userId;
    private Instant lastActiveAt;
  }

  @Getter
  @Builder
  public static class Detail {

    private UUID id;
    private UUID userId;
    private Instant lastActiveAt;
  }
}
