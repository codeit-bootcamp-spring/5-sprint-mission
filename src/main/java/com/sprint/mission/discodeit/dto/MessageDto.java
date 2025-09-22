package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

public class MessageDto {

  @Getter
  @Builder
  @Schema(name = "MessageCreateRequest")
  public static class CreateRequest {

    UUID channelId;
    UUID authorId;
    String content;
  }

  @Getter
  @Builder
  public static class CreateCommand {

    UUID channelId;
    UUID authorId;
    String content;
    List<MultipartFile> attachments;
  }

  @Getter
  @Builder
  @Schema(name = "MessageUpdateRequest")
  public static class UpdateRequest {

    String newContent;
  }

  @Getter
  @Builder
  public static class UpdateCommand {

    UUID id;
    String content;
  }

  @Getter
  @Builder
  @Schema(name = "MessageDetailResponse")
  public static class DetailResponse {

    UUID id;
    UserDto.DetailResponse author;
    ChannelDto.DetailResponse channel;
    String content;
    List<BinaryContentDto.DetailResponse> attachments;
    Instant createdAt;
    Instant updatedAt;
  }

  @Getter
  @Builder
  public static class Detail {

    UUID id;
    @Setter
    UserDto.Detail author;
    @Setter
    ChannelDto.Detail channel;
    String content;
    @Setter
    List<BinaryContentDto.Detail> attachments;
    Instant createdAt;
    Instant updatedAt;
  }
}
