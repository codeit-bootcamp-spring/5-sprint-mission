package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

public class ReadStatusDto {

  @Getter
  @Schema(name = "ReadStatusCreateRequest")
  public static class CreateRequest {

    UUID userId;
    UUID channelId;

    public CreateCommand toCommand() {
      return CreateCommand.builder().userId(this.userId).channelId(this.channelId).build();
    }
  }

  @Getter
  @Builder
  public static class CreateCommand {

    UUID userId;
    UUID channelId;
  }

  @Getter
  @Builder
  @Schema(name = "ReadStatusDetailResponse")
  public static class DetailResponse {

    UUID id;
    UUID userId;
    UUID channelId;
    Instant lastReadAt;
  }

  @Builder
  public static class Detail {

    UUID id;
    UUID userId;
    UUID channelId;
    Instant lastReadAt;

    public DetailResponse toResponse() {
      return DetailResponse.builder().id(id).userId(userId).channelId(channelId)
          .lastReadAt(lastReadAt).build();
    }
  }
}
