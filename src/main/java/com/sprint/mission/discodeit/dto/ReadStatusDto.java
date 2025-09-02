package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
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
    Instant lastReadAt;

    public CreateCommand toCommand() {
      return CreateCommand.builder()
                          .userId(this.userId)
                          .channelId(this.channelId)
                          .lastReadAt(this.lastReadAt)
                          .build();
    }
  }

  @Getter
  @Builder
  public static class CreateCommand {

    UUID userId;
    UUID channelId;
    Instant lastReadAt;

    public ReadStatus toEntity(User user, Channel channel) {
      return ReadStatus.builder()
                       .user(user)
                       .channel(channel)
                       .lastReadAt(this.lastReadAt)
                       .build();
    }
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
      if (this.id == null) {
        return null;
      }

      return DetailResponse.builder()
                           .id(this.id)
                           .userId(this.userId)
                           .channelId(this.channelId)
                           .lastReadAt(this.lastReadAt)
                           .build();
    }
  }
}
