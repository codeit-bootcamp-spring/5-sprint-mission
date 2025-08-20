package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

public class ChannelDto {

  @Getter
  public static class CreateRequest {

    String name;
    String description;
    List<UUID> participantIds;

    public Create toCreate(ChannelType type) {
      return Create.builder()
          .type(type)
          .name(this.name)
          .description(this.description)
          .participantIds(this.participantIds)
          .build();
    }
  }

  @Getter
  @Builder
  public static class Create {

    ChannelType type;
    String name;
    String description;
    UUID adminUserId;
    List<UUID> participantIds;
  }

  @Getter
  @Builder
  public static class UpdateRequest {

    String name;
    String description;
    List<UUID> participantIds;

    public Update toUpdate(UUID id) {
      return Update.builder()
          .id(id)
          .name(this.name)
          .description(this.description)
          .participantIds(this.participantIds)
          .build();
    }
  }

  @Getter
  @Builder
  public static class Update {

    UUID id;
    String name;
    String description;
    List<UUID> participantIds;
  }

  @Getter
  @Builder
  public static class DetailResponse {

    UUID id;
    ChannelType type;
    String name;
    String description;
    Instant lastMessageAt;
    List<UUID> participantIds;
  }

  @Getter
  @Builder
  public static class Detail {

    UUID id;
    ChannelType type;
    String name;
    String description;
    Instant lastMessageAt;
    List<UUID> participantIds;

    public DetailResponse toDetailResponse() {
      return DetailResponse.builder()
          .id(id)
          .type(type)
          .name(name)
          .description(description)
          .lastMessageAt(lastMessageAt)
          .participantIds(participantIds)
          .build();
    }
  }
}
