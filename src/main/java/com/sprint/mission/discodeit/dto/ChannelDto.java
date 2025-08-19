package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class ChannelDto {

  @Getter
  @Builder
  public static class CreateRequest {

    @Setter
    ChannelType type;
    String name;
    String description;
    UUID adminUserId;
    List<UUID> participantIds;
  }

  @Getter
  @Builder
  public static class UpdateRequest {

    @Setter
    UUID id;
    String name;
    String description;
    List<UUID> participantIds;
  }

  @Getter
  @Builder
  @ToString
  public static class DetailResponse {

    UUID id;
    ChannelType type;
    String name;
    String description;
    Instant lastMessageAt;
    List<UUID> participantIds;
  }
}
