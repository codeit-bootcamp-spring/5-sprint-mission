package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
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
  @Schema(name = "MessageCreateRequest")
  public static class CreateRequest {

    UUID channelId;
    UUID authorId;
    String content;

    public CreateCommand toCommand(List<MultipartFile> attachments) {
      return CreateCommand.builder()
                          .channelId(this.channelId)
                          .authorId(this.authorId)
                          .content(this.content)
                          .attachments(attachments)
                          .build();
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

    String newContent;

    public UpdateCommand toCommand(UUID id) {
      return UpdateCommand.builder()
                          .id(id)
                          .content(this.newContent)
                          .build();
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
