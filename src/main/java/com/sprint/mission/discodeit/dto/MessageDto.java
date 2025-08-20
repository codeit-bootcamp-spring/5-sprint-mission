package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public class MessageDto {

  @Getter
  @Schema(name = "MessageCreateRequest")
  public static class CreateRequest {

    UUID channelId;
    UUID authorId;
    String content;

    public CreateCommand toCommand(List<MultipartFile> attachments) {
      return CreateCommand.builder().channelId(this.channelId).authorId(this.authorId)
          .content(this.content).attachments(attachments).build();
    }
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
  @Schema(name = "MessageUpdateRequest")
  public static class UpdateRequest {

    String content;

    public UpdateCommand toCommand(UUID id) {
      return UpdateCommand.builder().id(id).content(this.content).build();
    }
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

    UUID channelId;
    UUID authorId;
    UUID id;
    String authorName;
    String channelName;
    String content;
    List<UUID> attachmentIds;
    Instant createdAt;
    Instant updatedAt;
  }

  @Getter
  @Builder
  public static class Detail {

    UUID channelId;
    UUID authorId;
    UUID id;
    String authorName;
    String channelName;
    String content;
    List<UUID> attachmentIds;
    Instant createdAt;
    Instant updatedAt;

    public DetailResponse toResponse() {
      return DetailResponse.builder().channelId(channelId).authorId(authorId).id(id)
          .authorName(authorName).channelName(channelName).content(content)
          .attachmentIds(attachmentIds).createdAt(createdAt).updatedAt(updatedAt).build();
    }
  }
}
