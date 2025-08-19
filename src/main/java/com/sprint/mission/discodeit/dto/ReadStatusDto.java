package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class ReadStatusDto {

  @Getter
  @Builder
  public static class CreateRequest {

    UUID userId;
    UUID channelId;
  }

  @Getter
  @Builder
  @ToString
  public static class DetailResponse {

    UUID id;
    UUID userId;
    UUID channelId;
    Instant lastReadAt;
  }
}
