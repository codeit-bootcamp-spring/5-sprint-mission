package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

public class ChannelDto {

  @Getter
  @Schema(name = "ChannelCreateRequest")
  public static class CreateRequest {

    String name;
    String description;
    List<UUID> participantIds;

    public CreateCommand toCommand(ChannelType type) {
      return CreateCommand.builder()
                          .type(type)
                          .name(this.name)
                          .description(this.description)
                          .participantIds(this.participantIds)
                          .build();
    }
  }

  @Getter
  @Builder
  public static class CreateCommand {

    ChannelType type;
    String name;
    String description;
    UUID adminUserId;
    List<UUID> participantIds;

    public Channel toEntity() {
      return Channel.builder()
                    .type(this.type)
                    .name(this.name)
                    .description(this.description)
                    .build();
    }
  }

  @Getter
  @Builder
  @Schema(name = "ChannelUpdateRequest")
  public static class UpdateRequest {

    String name;
    String description;
    List<UUID> participantIds;

    public UpdateCommand toCommand(UUID id) {
      return UpdateCommand.builder()
                          .id(id)
                          .name(this.name)
                          .description(this.description)
                          .participantIds(this.participantIds)
                          .build();
    }
  }

  @Getter
  @Builder
  public static class UpdateCommand {

    UUID id;
    String name;
    String description;
    List<UUID> participantIds;
  }

  @Getter
  @Builder
  @Schema(name = "ChannelDetailResponse")
  public static class DetailResponse {

    UUID id;
    ChannelType type;
    String name;
    String description;
    Instant lastMessageAt;
    List<UserDto.DetailResponse> participants;
  }

  @Getter
  @Builder
  public static class Detail {

    UUID id;
    ChannelType type;
    String name;
    String description;
    Instant lastMessageAt;
    List<UserDto.Detail> participants;

    public DetailResponse toResponse() {
      if (this.id == null) {
        return null;
      }

      return DetailResponse.builder()
                           .id(this.id)
                           .type(this.type)
                           .name(this.name)
                           .description(this.description)
                           .lastMessageAt(this.lastMessageAt)
                           .participants(this.participants.stream()
                                                          .map(UserDto.Detail::toResponse)
                                                          .toList())
                           .build();
    }
  }
}
