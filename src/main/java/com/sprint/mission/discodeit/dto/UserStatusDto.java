package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class UserStatusDto {

  @Getter
  @Builder
  public static class CreateRequest {

    private UUID userId;
  }

  @Getter
  @Builder
  @ToString
  public static class DetailResponse {

    private UUID id;
    private UUID userId;
    private Instant lastLogin;
  }
}
